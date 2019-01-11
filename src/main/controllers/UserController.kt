package main.controllers

import kotlinserverless.framework.controllers.RestController
import kotlinserverless.framework.controllers.DefaultController
import kotlinserverless.framework.models.Request
import kotlinserverless.framework.services.SOAResult
import main.daos.User
import kotlinserverless.main.services.users.UserHelloService
import main.daos.UserAccount

class UserController: DefaultController<User>(), RestController<User, UserAccount> {
    fun hello(user: UserAccount, request: Request): SOAResult<String> {
        return UserHelloService.execute()
    }
}