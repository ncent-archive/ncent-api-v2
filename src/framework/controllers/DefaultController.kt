package kotlinserverless.framework.controllers

import framework.models.BaseIntEntity
import kotlinserverless.framework.models.*
import kotlinserverless.framework.services.SOAResult

open class DefaultController<T: BaseIntEntity> : Controller<T> {
	override fun <T : BaseIntEntity> defaultRouting(
			cls: Class<T>,
            request: Request,
            restController: RestController<T, ApiUser>
    ): SOAResult<T> {
		val path = request.input["path"].toString().removePrefix("/").split("/")
		val function = path[1]
		val func = restController::class.members.find { it.name == function }
		if(path.size > 1 && func != null) {
			try {
				return func.call(restController, AnonymousUser(), request) as SOAResult<T>
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