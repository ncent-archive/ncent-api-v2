package kotlinserverless.framework.dispatchers

import kotlinserverless.framework.models.*
import java.io.File
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import main.services.user_account.GetUserAccountService
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.mockito.internal.matchers.Not
import java.lang.Exception
import java.lang.reflect.InvocationTargetException
import kotlin.reflect.full.createInstance

/**
 * Request Dispatcher implementation
 */
open class RequestDispatcher: Dispatcher<ApiGatewayRequest, Any> {
    @Throws(RouterException::class, NotFoundException::class, ForbiddenException::class)
    override fun locate(request: ApiGatewayRequest): Any? {
        val path = request.input["path"]
        for ((regex, inputModel, outputModel, controller) in ROUTER.routes) {
			if (!Regex(regex).matches(path as CharSequence)) {
				continue
			}

            val outputModelClass = Class.forName(outputModel).kotlin
            val controllerClass = Class.forName(controller).kotlin
            val controllerInstance = controllerClass.createInstance()

            val func = controllerClass.members.find { it.name == "defaultRouting" }
            val user = findUserByRequest(request)
            val result = try {
                func?.call(
                        controllerInstance,
                        inputModel,
                        outputModelClass::class.java,
                        request!!,
                        user,
                        controllerInstance
                ) as SOAResult<Any>
            }
            catch(e: InvocationTargetException) {
                throw e.targetException
            }

            if(result.result == SOAResultType.SUCCESS) {
                return result.data
            } else if (result.result == SOAResultType.FAILURE && result.message != null) {
                throw ForbiddenException(result.message as String)
            } else {
                println("There was an error processing the request.")
                println(result.message)
                throw InternalError(result.message)
            }
        }
		
		throw RouterException(path as? String ?: "")
    }

    fun findUserByRequest(request: Request) : UserAccount {
        var data = request.input.map { Pair(it.key, it.value.toString()) }.toMap()

        val userResult = GetUserAccountService.execute(data["userId"]?.toInt(), data["email"], data["apiKey"])
        if(userResult.result == SOAResultType.SUCCESS) {
            return userResult.data!!
        } else {
            throw Exception(userResult.message)
        }
    }

    /**
     * Singleton that loads the routes once and keep them on memory
     */
    companion object BackendRouter {
		// this is not ideal and should user get resources, but having issues getting
		// maven to load them properly
        private val FILE = File("src/main/resources/yml/routes.yml")
        val ROUTER: Routes = ObjectMapper(YAMLFactory()).readValue(FILE, Routes::class.java)
    }
}