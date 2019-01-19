package kotlinserverless.framework.controllers

import framework.models.BaseIntEntity
import kotlinserverless.framework.models.*
import kotlinserverless.framework.services.SOAResult
import main.daos.UserAccount
import main.helpers.ControllerHelper
import java.lang.reflect.InvocationTargetException

open class DefaultController<T: BaseIntEntity> : Controller<T> {
	override fun <T : BaseIntEntity> defaultRouting(
			incls: String,
			outcls: Class<T>,
            requestData: ControllerHelper.RequestData,
			user: UserAccount?,
            restController: RestController<T, UserAccount>
    ): SOAResult<*> {
		val path = requestData.request.input["path"].toString().removePrefix("/").split("/")

		if(path.size > 1) {
			val func = restController::class.members.find { it.name == path[1] }
			if(func != null) {
				return try {
					func.call(restController, user, requestData) as SOAResult<T>
				}
				catch(e: InvocationTargetException) {
					throw e.targetException
				}
				catch(e: Exception) {
					println("There was an error routing!")
					println(e)
					super.defaultRouting(incls, outcls, requestData, user, restController)
				}
			}
		}
		return super.defaultRouting(incls, outcls, requestData, user, restController)
	}
}