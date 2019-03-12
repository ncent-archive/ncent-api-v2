package main.controllers

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
        return if (requestData.queryParams["email"] != null) {
            GetUserAccountService.execute(null, requestData.queryParams["email"] as String)
        } else if (id != null) {
            GetUserAccountService.execute(id)
        } else {
            SOAResult(SOAResultType.FAILURE, null)
        }
    }

    fun balances(user: UserAccount, requestData: RequestData): SOAResult<ChallengeToUnsharedTransactionsList> {
        val challengesResult = ChallengeHelper.getChallenges(user)

        if (challengesResult.data == null) {
            throw InternalError()
        }

        return SOAResult(SOAResultType.SUCCESS, challengesResult.message, challengesResult.data!!)
    }

    override fun create(user: UserAccount?, requestData: RequestData): SOAResult<NewUserAccount> {
        return GenerateUserAccountService.execute(
            requestData.body["email"] as String,
            requestData.body["firstname"] as String,
            requestData.body["lastname"] as String)
    }

    override fun delete(user: UserAccount, requestData: RequestData): SOAResult<Boolean> {
        return DeleteUserAccountService.execute(user)
    }

    fun reset(user: UserAccount, requestData: RequestData): SOAResult<NewUserAccount> {
        return ResetUserAccount.execute(user)
    }
}