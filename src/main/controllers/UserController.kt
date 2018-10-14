package main.controllers

import kotlinserverless.framework.controllers.RestController
import kotlinserverless.framework.controllers.DefaultController
import kotlinserverless.framework.models.Request
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.main.daos.User
import kotlinserverless.main.services.users.UserHelloService

class UserController: DefaultController<User>(), RestController<User, User> {
    fun hello(user: User, request: Request): SOAResult<String> {
        return UserHelloService().execute(123, "HELLO", HashMap())
    }
}