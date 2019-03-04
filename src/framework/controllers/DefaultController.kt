package kotlinserverless.framework.controllers

import framework.models.BaseIntEntity
import framework.services.DaoService
import kotlinserverless.framework.models.*
import kotlinserverless.framework.services.SOAResult
import main.daos.UserAccount
import main.helpers.ControllerHelper
import main.services.user_account.ValidateApiKeyService
import java.lang.reflect.InvocationTargetException

open class DefaultController<T: BaseIntEntity> : Controller<T> {
	@Throws(ForbiddenException::class)
	fun validateApiKey(user: UserAccount, requestData: ControllerHelper.RequestData) {
		if(requestData.userAuth == null)
			throw UnauthorizedError("Must include authentication for this endpoint")
		val validateApiKeyResult = DaoService.execute {
			ValidateApiKeyService.execute(user, requestData.userAuth.secretKey)
		}
		DaoService.throwOrReturn(validateApiKeyResult.result, validateApiKeyResult.message)
	}

	@Throws(ForbiddenException::class)
	override fun <T : BaseIntEntity> defaultRouting(
		incls: String,
		outcls: Class<T>,
		requestData: ControllerHelper.RequestData,
		user: UserAccount?,
		restController: RestController<T, UserAccount>,
		method: String,
		shouldValidatePost: Boolean,
		shouldValidatePut: Boolean
    ): SOAResult<*> {
		val pathString = requestData.request.input["path"].toString()
		when(method) {
			ControllerHelper.HTTP_POST -> {
				if(shouldValidatePost)
					validateApiKey(user!!, requestData)
			}
			ControllerHelper.HTTP_PUT -> {
				if(shouldValidatePut)
					validateApiKey(user!!, requestData)
			}
			else -> validateApiKey(user!!, requestData)
		}

		val path = pathString.removePrefix("/").split("/")

		if(path.size > 1) {
			val func = restController::class.members.find { it.name == path[1] }
			if(func != null) {
				return try {
					func.call(restController, user, requestData) as SOAResult<T>
				}
				catch(e: InvocationTargetException) {
					Handler.log(e, e.message)
					throw e.targetException
				}
				catch(e: Exception) {
					Handler.log(e, "There was an error routing")
					super.defaultRouting(incls, outcls, requestData, user, restController, method, shouldValidatePost, shouldValidatePut)
				}
			}
		}
		return super.defaultRouting(incls, outcls, requestData, user, restController, method, shouldValidatePost, shouldValidatePut)
	}
}