package kotlinserverless.framework.controllers

import kotlinserverless.framework.controllers.DefaultController
import kotlinserverless.framework.services.Service
import kotlinserverless.framework.*

interface DefaultController<T: Model> : Controller<T> {
//	inline fun <reified M: T> execute(request: Request?) : Any {
//        val service: Service<M, ApiUser> = ServiceFactory<M>().getService(M::class.java)
//        return defaultRouting(M::class.java, request!!, service)
//    }
	
	override fun <T : Model> defaultRouting(cls: Class<T>, request: Request, service: Service<T, ApiUser>): Any? {
		val path = request.input["path"].toString().removePrefix("/").split("/")
		val function = path[1]
		val func = service::class.members.find { it.name == function }
		if(path.size > 1 && func != null) {
			try {
				return func.call(service, AnonymousUser(), request)
			}
			catch(e: Exception) {
				println("There was an error: " + e)
				return super.defaultRouting(cls, request, service)
			}
		}
		return super.defaultRouting(cls, request, service)
	}
}