package main.controllers

import framework.services.DaoService
import kotlinserverless.framework.controllers.RestController
import kotlinserverless.framework.controllers.DefaultController
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import main.daos.NewUserAccount
import main.helpers.ChallengeHelper
import main.helpers.ControllerHelper.RequestData
import main.services.user_account.*

class UserAccountController: DefaultController<UserAccount>(), RestController<UserAccount, UserAccount> {
    override fun findOne(user: UserAccount, requestData: RequestData, id: Int?): SOAResult<UserAccount> {
        validateApiKey(user, requestData)
        return DaoService.execute {
            var result = SOAResult<UserAccount?>(SOAResultType.FAILURE, null)
            if (requestData.queryParams["email"] != null) {
                result = GetUserAccountService.execute(null, requestData.queryParams["email"] as String)
            } else if (id != null) {
                result = GetUserAccountService.execute(id)
            }
            DaoService.throwOrReturn(result.result, result.message)
            return@execute result.data!!
        }
    }

    fun balances(user: UserAccount, requestData: RequestData): SOAResult<ChallengeToUnsharedTransactionsList> {
        validateApiKey(user, requestData)

        val challengesResult = ChallengeHelper.getChallenges(user)

        if (challengesResult.data == null) {
            throw InternalError()
        }

        return SOAResult(SOAResultType.SUCCESS, challengesResult.message, challengesResult.data!!)
    }

    override fun create(user: UserAccount?, requestData: RequestData): SOAResult<NewUserAccount> {
        return DaoService.execute {
            val result = GenerateUserAccountService.execute(
                requestData.body["email"]!! as String,
                requestData.body["firstname"]!! as String,
                requestData.body["lastname"]!! as String)
            DaoService.throwOrReturn(result.result, result.message)
            return@execute result.data!!
        }
    }

    override fun delete(user: UserAccount, requestData: RequestData): SOAResult<Boolean?> {
        validateApiKey(user!!, requestData)

        return DaoService.execute {
            val result = DeleteUserAccountService.execute(user)
            DaoService.throwOrReturn(result.result, result.message)
            return@execute result.data
        }
    }

    fun reset(user: UserAccount, requestData: RequestData): SOAResult<NewUserAccount> {
        validateApiKey(user, requestData)
        return DaoService.execute {
            val result = ResetUserAccount.execute(user)
            DaoService.throwOrReturn(result.result, result.message)
            return@execute result.data!!
        }
    }
}