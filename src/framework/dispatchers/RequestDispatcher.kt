package kotlinserverless.framework.dispatchers

import kotlinserverless.framework.models.*
import java.io.File
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.main.daos.User
import kotlin.reflect.full.createInstance

/**
 * Request Dispatcher implementation
 */
open class RequestDispatcher: Dispatcher<ApiGatewayRequest, Any> {

    lateinit var defaultUser: User

    @Throws(RouterException::class)
    override fun locate(request: ApiGatewayRequest): Any? {
        val path = request.input["path"]
        for ((regex, model, controller) in ROUTER.routes) {
			if (!Regex(regex).matches(path as CharSequence)) {
				continue
			}

            val modelClass = Class.forName(model).kotlin
            val controllerClass = Class.forName(controller).kotlin
            val controllerInstance = controllerClass.createInstance()

            val func = controllerClass.members.find { it.name == "defaultRouting" }
            val user = findUserByRequest(request)
            val result = func?.call(
                    controllerInstance,
                    modelClass::class.java,
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

    fun findUserByRequest(request: Request) : User {
        // TODO refactor to get user by request from database
        return defaultUser
    }

    /**
     * Singleton that loads the routes once and keep them on memory
     */
    companion object BackendRouter {
		// this is not ideal and should use get resources, but having issues getting
		// maven to load them properly
        private val FILE = File("src/main/resources/yml/routes.yml")
        val ROUTER: Routes = ObjectMapper(YAMLFactory()).readValue(FILE, Routes::class.java)
    }
}