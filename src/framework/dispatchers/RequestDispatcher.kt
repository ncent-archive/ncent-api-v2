package kotlinserverless.framework.dispatchers

import kotlinserverless.framework.*
import java.io.File
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import kotlin.reflect.full.createInstance
import kotlinserverless.framework.controllers.Controller
import kotlinserverless.framework.controllers.DefaultController

/**
 * Request Dispatcher implementation
 */
open class RequestDispatcher: Dispatcher<ApiGatewayRequest, Any> {

    @Throws(RouterException::class)
    override fun locate(key: ApiGatewayRequest): Any? {
        val path = key.input["path"]

        var response: Any? = null
        var found: Boolean = false

        for ((regex, function, cls) in ROUTER.routes) {
            if (Regex(regex).matches(path as CharSequence)) {
                val kClass = Class.forName(cls).kotlin
				val func = kClass.members.find { it.name == function }
                response = func?.call(kClass.createInstance(), key)
                found = true

                break
            }
        }

        if (!found)
            throw RouterException(path as? String ?: "")

        return response
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