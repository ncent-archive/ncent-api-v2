package main.userAccounts.controllers


import kotlinserverless.framework.controllers.RestController
import kotlinserverless.framework.controllers.DefaultController
import kotlinserverless.framework.models.ApiUser
import kotlinserverless.framework.services.SOAResult
import main.userAccounts.models.UserAccount
import kotlinserverless.framework.models.Request

class UserAccountController: DefaultController<UserAccount>(), RestController<UserAccount, ApiUser> {
    override fun findOne(user: ApiUser, id: Int): SOAResult<UserAccount> {
        throw NotImplementedError()
    }

    override fun create(user: ApiUser, element: UserAccount): SOAResult<UserAccount> {
        throw NotImplementedError()
    }

    fun login(user: ApiUser, request: Request): SOAResult<UserAccount> {
        throw NotImplementedError()
    }

    fun logout(user: ApiUser, request: Request): SOAResult<UserAccount> {
        throw NotImplementedError()
    }
}