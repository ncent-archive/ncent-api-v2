package kotlinserverless.framework.dispatchers

import kotlinserverless.framework.*
import java.io.File
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import kotlinserverless.framework.controllers.DefaultController
import kotlinserverless.framework.healthchecks.models.Healthcheck
import kotlinserverless.framework.healthchecks.services.HealthcheckService
import kotlinserverless.main.users.models.User
import kotlinserverless.main.users.services.UserService

/**
 * Request Dispatcher implementation
 */
open class RequestDispatcher: Dispatcher<ApiGatewayRequest, Any> {

    @Throws(RouterException::class)
    override fun locate(request: ApiGatewayRequest): Any? {
        val path = request.input["path"]

        for ((regex, model) in ROUTER.routes) {
			if (!Regex(regex).matches(path as CharSequence)) {
				continue
			}

            return when(Class.forName(model)) {
                Healthcheck::class.java -> DefaultController<Healthcheck>().defaultRouting(
                    Healthcheck::class.java,
                    request!!,
                    HealthcheckService()
                )
                User::class.java -> DefaultController<User>().defaultRouting(
                    User::class.java,
                    request!!,
                    UserService()
                )
                else -> throw RouterException(path as? String ?: "")
            }
        }
		
		throw RouterException(path as? String ?: "")
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