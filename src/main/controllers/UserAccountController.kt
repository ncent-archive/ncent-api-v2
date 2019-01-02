package main.controllers

import kotlinserverless.framework.controllers.RestController
import kotlinserverless.framework.controllers.DefaultController
import kotlinserverless.framework.services.SOAResult
import main.daos.UserAccount
import kotlinserverless.framework.models.Request
import main.daos.NewUserAccount
import main.services.user_account.GenerateUserAccountService

class UserAccountController: DefaultController<UserAccount>(), RestController<UserAccount, UserAccount> {
    override fun findOne(user: UserAccount, id: Int): SOAResult<UserAccount> {
        throw NotImplementedError()
    }

    override fun create(user: UserAccount, params: Map<String, String>): SOAResult<NewUserAccount> {
        return GenerateUserAccountService.execute(null, params)
    }

    fun login(user: UserAccount, request: Request): SOAResult<UserAccount> {
        throw NotImplementedError()
    }

    fun logout(user: UserAccount, request: Request): SOAResult<UserAccount> {
        throw NotImplementedError()
    }
}