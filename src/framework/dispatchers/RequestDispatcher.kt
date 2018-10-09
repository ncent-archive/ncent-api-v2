package kotlinserverless.framework.dispatchers

import kotlinserverless.framework.models.*
import java.io.File
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import org.jetbrains.exposed.sql.Database
import kotlin.reflect.full.createInstance

/**
 * Request Dispatcher implementation
 */
open class RequestDispatcher: Dispatcher<ApiGatewayRequest, Any> {

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

            val result = func?.call(
                    controllerInstance,
                    modelClass::class.java,
                    request!!,
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

    /**
     * Singleton that loads the routes once and keep them on memory
     * Also loads the database connection
     */
    companion object BackendRouter {
		// this is not ideal and should use get resources, but having issues getting
		// maven to load them properly
        private val FILE = File("src/main/resources/yml/routes.yml")
        val ROUTER: Routes = ObjectMapper(YAMLFactory()).readValue(FILE, Routes::class.java)

        val db = Database.connect(
                System.getenv("database_url") ?: "jdbc:h2:mem:test",
                driver = System.getenv("database_driver") ?: "org.h2.Driver",
                user = System.getenv("database_user") ?: "",
                password = System.getenv("database_password") ?: ""
        )
    }
}