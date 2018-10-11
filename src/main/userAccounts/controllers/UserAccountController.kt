package main.userAccounts.controllers


import kotlinserverless.framework.controllers.RestController
import kotlinserverless.framework.controllers.DefaultController
import kotlinserverless.framework.services.SOAResult
import main.userAccounts.models.UserAccount
import kotlinserverless.framework.models.Request
import kotlinserverless.main.users.models.User

class UserAccountController: DefaultController<UserAccount>(), RestController<UserAccount, User> {
    override fun findOne(user: User, id: Int): SOAResult<UserAccount> {
        throw NotImplementedError()
    }

    override fun create(user: User, element: UserAccount): SOAResult<UserAccount> {
        throw NotImplementedError()
    }

    fun login(user: User, request: Request): SOAResult<UserAccount> {
        throw NotImplementedError()
    }

    fun logout(user: User, request: Request): SOAResult<UserAccount> {
        throw NotImplementedError()
    }
}