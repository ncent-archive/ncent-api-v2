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
        val result = DaoService.execute {
            return@execute if (requestData.queryParams["email"] != null) {
                GetUserAccountService.execute(null, requestData.queryParams["email"] as String)
            } else if (id != null) {
                GetUserAccountService.execute(id)
            } else {
                SOAResult(SOAResultType.FAILURE, null)
            }
        }
        DaoService.throwOrReturn(result)
        return result.data!!
    }

    fun balances(user: UserAccount, requestData: RequestData): SOAResult<ChallengeToUnsharedTransactionsList> {
        val challengesResult = ChallengeHelper.getChallenges(user)

        if (challengesResult.data == null) {
            throw InternalError()
        }

        return SOAResult(SOAResultType.SUCCESS, challengesResult.message, challengesResult.data!!)
    }

    override fun create(user: UserAccount?, requestData: RequestData): SOAResult<NewUserAccount> {
        val result = DaoService.execute {
            GenerateUserAccountService.execute(
                requestData.body["email"] as String,
                requestData.body["firstname"] as String,
                requestData.body["lastname"] as String)
        }
        DaoService.throwOrReturn(result)
        return result.data!!
    }

    override fun delete(user: UserAccount, requestData: RequestData): SOAResult<Boolean> {
        val result = DaoService.execute {
            DeleteUserAccountService.execute(user)
        }
        DaoService.throwOrReturn(result)
        return result.data!!
    }

    fun reset(user: UserAccount, requestData: RequestData): SOAResult<NewUserAccount> {
        val result = DaoService.execute {
            ResetUserAccount.execute(user)
        }
        DaoService.throwOrReturn(result)
        return result.data!!
    }
}