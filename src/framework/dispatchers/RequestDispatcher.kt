package kotlinserverless.framework.dispatchers

import kotlinserverless.framework.models.*
import java.io.File
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.reflect.full.createInstance

/**
 * Request Dispatcher implementation
 */
open class RequestDispatcher: Dispatcher<ApiGatewayRequest, Any> {
    @Throws(RouterException::class)
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
            val result = func?.call(
                    controllerInstance,
                    inputModel,
                    outputModelClass::class.java,
                    request!!,
                    user,
                    controllerInstance
            ) as SOAResult<Any>

            if(result.result == SOAResultType.SUCCESS) {
                return result.data
            } else {
                println("There was an error processing the request.")
                println(result.message)
                throw InternalError(result.message)
            }
        }
		
		throw RouterException(path as? String ?: "")
    }

    fun findUserByRequest(request: Request) : UserAccount {
        // TODO move this logic to the GetUserAccountService logic!
        return when {
            request.input.containsKey("userId") -> {
                transaction {
                    UserAccount.findById((request.input["userId"] as String).toInt())!!
                }
            }
            request.input.containsKey("email") -> {
                transaction {
                    val query = UserAccounts
                            .innerJoin(Users)
                            .select {
                                Users.email eq (request.input["email"] as String)
                            }
                    UserAccount.wrapRows(query).toList().distinct().first()
                }
            }
            request.input.containsKey("apiKey") -> {
                val query = UserAccounts
                        .innerJoin(ApiCreds)
                        .select {
                            ApiCreds.apiKey eq (request.input["apiKey"] as String)
                        }
                UserAccount.wrapRows(query).toList().distinct().first()
            }
            else -> throw Exception("Could not find user without email/apiKey/userId")
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