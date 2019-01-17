package kotlinserverless.framework.controllers

import framework.models.BaseIntEntity
import kotlinserverless.framework.models.*
import kotlinserverless.framework.services.SOAResult
import main.daos.UserAccount
import java.lang.reflect.InvocationTargetException

open class DefaultController<T: BaseIntEntity> : Controller<T> {
	override fun <T : BaseIntEntity> defaultRouting(
			incls: String,
			outcls: Class<T>,
            request: Request,
			user: UserAccount,
            restController: RestController<T, UserAccount>
    ): SOAResult<*> {
		val path = request.input["path"].toString().removePrefix("/").split("/")

		if(path.size > 1) {
			val func = restController::class.members.find { it.name == path[1] }
			if(func != null) {
				try {
					return func.call(restController, user, request) as SOAResult<T>
				}
				catch(e: InvocationTargetException) {
					throw e.targetException
				}
				catch(e: Exception) {
					println("There was an error routing!")
					println(e)
					return super.defaultRouting(incls, outcls, request, user, restController)
				}
			}
		}
		return super.defaultRouting(incls, outcls, request, user, restController)
	}
}