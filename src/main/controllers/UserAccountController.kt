package main.controllers

import framework.models.idValue
import framework.services.DaoService
import kotlinserverless.framework.controllers.RestController
import kotlinserverless.framework.controllers.DefaultController
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import main.daos.NewUserAccount
import main.helpers.ControllerHelper.RequestData
import main.services.user_account.GenerateUserAccountService
import main.services.user_account.GetUserAccountService
import main.services.user_account.StartSessionService
import main.services.user_account.EndSessionService

class UserAccountController: DefaultController<UserAccount>(), RestController<UserAccount, UserAccount> {
    override fun findOne(user: UserAccount, requestData: RequestData, id: Int): SOAResult<UserAccount> {
        validateApiKey(user, requestData)
        return DaoService.execute {
            val result = GetUserAccountService.execute(user.idValue)
            DaoService.throwOrReturn(result.result, result.message)
            return@execute result.data!!
        }
    }

    override fun create(user: UserAccount, requestData: RequestData): SOAResult<NewUserAccount> {
        return DaoService.execute {
            val result = GenerateUserAccountService.execute(
                requestData.body["email"]!! as String,
                requestData.body["firstname"]!! as String,
                requestData.body["lastname"]!! as String)
            DaoService.throwOrReturn(result.result, result.message)
            return@execute result.data!!
        }
    }

    fun login(user: UserAccount, requestData: RequestData): SOAResult<UserAccount> {
        validateApiKey(user, requestData)

        val result = SOAResult<UserAccount>(SOAResultType.FAILURE, null, null)

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

    fun logout(user: UserAccount, requestData: RequestData): SOAResult<UserAccount> {
        validateApiKey(user, requestData)

        val result = SOAResult<UserAccount>(
            SOAResultType.FAILURE,
            "",
            null
        )

        val endSessionResult = EndSessionService.execute(user.session.sessionKey)

        if (endSessionResult.result != SOAResultType.SUCCESS) {
            result.message = endSessionResult.message
            return result
        }

        result.data = user
        result.result = SOAResultType.SUCCESS

        return result
    }
}