package main.userAccounts.controllers


import kotlinserverless.framework.controllers.RestController
import kotlinserverless.framework.controllers.DefaultController
import kotlinserverless.framework.models.ApiUser
import main.userAccounts.models.UserAccount

class UserAccountController: DefaultController<UserAccount>(), RestController<UserAccount, ApiUser> {
    override fun findOne(user: ApiUser, id: Int): UserAccount {
        throw NotImplementedError()
    }
}