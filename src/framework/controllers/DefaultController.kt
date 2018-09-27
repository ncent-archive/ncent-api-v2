package kotlinserverless.framework.controllers

import kotlinserverless.framework.models.*

class DefaultController<T: Model> : Controller<T> {
	override fun <T : Model> defaultRouting(
			cls: Class<T>,
            request: Request,
            restController: RestController<T, ApiUser>
    ): Any? {
		val path = request.input["path"].toString().removePrefix("/").split("/")
		val function = path[1]
		val func = restController::class.members.find { it.name == function }
		if(path.size > 1 && func != null) {
			try {
				return func.call(restController, AnonymousUser(), request)
			}
			catch(e: Exception) {
				println("There was an error routing!")
				println(e)
				return super.defaultRouting(cls, request, restController)
			}
		}
		return super.defaultRouting(cls, request, restController)
	}
}