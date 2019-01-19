package kotlinserverless.framework.controllers

import framework.services.DaoService
import kotlinserverless.framework.models.UnauthorizedError
import main.daos.UserAccount
import main.helpers.ControllerHelper.RequestData
import main.services.user_account.ValidateApiKeyService

/**
 * Service that exposes the capabilities of a {@link T} element
 * @param <K> Natural Key type
 * @param <T> Element type
 * @param <F> Filter type
 * @param <U> User permissions
 */
interface RestController<T, U> : ReadableController<T, U>, WritableController<T, U> {
    fun validateApiKey(user: UserAccount, requestData: RequestData) {
        if(requestData.userAuth == null)
            throw UnauthorizedError("Must include authentication for this endpoint")
        val validateApiKeyResult = DaoService.execute {
            ValidateApiKeyService.execute(user, requestData.userAuth.secretKey)
        }
        DaoService.throwOrReturn(validateApiKeyResult.result, validateApiKeyResult.message)
    }
}