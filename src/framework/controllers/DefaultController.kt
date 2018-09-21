package kotlinserverless.framework.controllers

import kotlinserverless.framework.services.Service
import kotlinserverless.framework.models.*

class DefaultController<T: Model> : Controller<T> {
	override fun <T : Model> defaultRouting(
			cls: Class<T>,
            request: Request,
            service: Service<T, ApiUser>
    ): Any? {
		val path = request.input["path"].toString().removePrefix("/").split("/")
		val function = path[1]
		val func = service::class.members.find { it.name == function }
		if(path.size > 1 && func != null) {
			try {
				return func.call(service, AnonymousUser(), request)
			}
			catch(e: Exception) {
				println("FOUND AN ERROR")
				println(e)
				println("There was an error: " + e)
				return super.defaultRouting(cls, request, service)
			}
		}
		return super.defaultRouting(cls, request, service)
	}
}