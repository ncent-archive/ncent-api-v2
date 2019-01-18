package kotlinserverless.framework.controllers

import framework.services.DaoService
import kotlinserverless.framework.models.Request
import main.daos.UserAccount
import main.helpers.ControllerHelper
import main.services.user_account.ValidateApiKeyService

/**
 * Service that exposes the capabilities of a {@link T} element
 * @param <K> Natural Key type
 * @param <T> Element type
 * @param <F> Filter type
 * @param <U> User permissions
 */
interface RestController<T, U> : ReadableController<T, U>, WritableController<T, U> {
    fun validateApiKey(user: UserAccount, queryParams: Map<String, Any>) {
        val validateApiKeyResult = DaoService.execute {
            ValidateApiKeyService.execute(user, queryParams["secretKey"]!! as String)
        }
        DaoService.throwOrReturn(validateApiKeyResult.result, validateApiKeyResult.message)
    }

    fun validateApiKeyAndGetQueryParams(user: UserAccount, request: Request): Map<String, Any> {
        val queryParameters: Map<String, Any> = ControllerHelper.getQueryStringParameters(request)
        validateApiKey(user, queryParameters)
        return queryParameters
    }
}