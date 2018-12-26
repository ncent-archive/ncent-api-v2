package kotlinserverless.framework.controllers

import framework.models.BaseIntEntity
import kotlinserverless.framework.models.*
import kotlinserverless.framework.services.SOAResult
import main.daos.User

open class DefaultController<T: BaseIntEntity> : Controller<T> {
	override fun <T : BaseIntEntity> defaultRouting(
			cls: Class<T>,
            request: Request,
			user: User,
            restController: RestController<T, User>
    ): SOAResult<T> {
		val path = request.input["path"].toString().removePrefix("/").split("/")

		if(path.size > 1) {
			val func = restController::class.members.find { it.name == path[1] }
			if(func != null) {
				try {
					return func.call(restController, user, request) as SOAResult<T>
				}
				catch(e: Exception) {
					println("There was an error routing!")
					println(e)
					return super.defaultRouting(cls, request, user, restController)
				}
			}
		}
		return super.defaultRouting(cls, request, user, restController)
	}
}