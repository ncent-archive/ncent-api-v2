package main.controllers

import framework.models.idValue
import kotlinserverless.framework.controllers.RestController
import kotlinserverless.framework.controllers.DefaultController
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import kotlinserverless.framework.models.Request
import main.daos.NewUserAccount
import main.services.user_account.GenerateUserAccountService
import main.services.user_account.GetUserAccountService
import main.services.user_account.ValidateApiKeyService
import main.services.user_account.StartSessionService
import main.services.user_account.EndSessionService

class UserAccountController: DefaultController<UserAccount>(), RestController<UserAccount, UserAccount> {
    override fun findOne(user: UserAccount, id: Int): SOAResult<UserAccount> {
        return GetUserAccountService.execute(user.idValue, id, null)
    }

    override fun create(user: UserAccount, params: Map<String, String>): SOAResult<NewUserAccount> {
        return GenerateUserAccountService.execute(user.idValue, params)
    }

    fun login(user: UserAccount, request: Request): SOAResult<UserAccount> {
        val result = SOAResult<UserAccount>(
                SOAResultType.FAILURE,
                "",
                null
        )

        val apiCred = user.apiCreds
        val apiKeyParams = mutableMapOf(
                Pair("apiKey", apiCred.apiKey),
                Pair("secretKey", apiCred.secretKey)
        )

        ValidateApiKeyService.execute(user.idValue, Any(), apiKeyParams)

        //TODO: Full session implementation
        val startSessionResult = StartSessionService.execute()
        if (startSessionResult.result != SOAResultType.SUCCESS) {
            result.message = "Failed to start session"
        } else {
            result.result = SOAResultType.SUCCESS
            result.data = user
        }

        return result
    }

    fun logout(user: UserAccount, request: Request): SOAResult<UserAccount> {
        val result = SOAResult<UserAccount>(
                SOAResultType.FAILURE,
                "",
                null
        )

        val apiCred = user.apiCreds
        val apiKeyParams = mutableMapOf(
                Pair("apiKey", apiCred.apiKey),
                Pair("secretKey", apiCred.secretKey)
        )

        ValidateApiKeyService.execute(user.idValue, Any(), apiKeyParams)

        val endSessionResult = EndSessionService.execute(user.idValue, user.session.sessionKey)

        if (endSessionResult.result != SOAResultType.SUCCESS) {
            result.message = endSessionResult.message
            return result
        }

        result.data = user
        result.result = SOAResultType.SUCCESS

        return result
    }
}