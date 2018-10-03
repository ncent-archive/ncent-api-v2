package main.users.controllers

import kotlinserverless.framework.controllers.RestController
import kotlinserverless.framework.controllers.DefaultController
import kotlinserverless.framework.models.ApiUser
import kotlinserverless.framework.models.Request
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.main.users.models.User
import kotlinserverless.main.users.services.UserHelloService

class UserController: DefaultController<User>(), RestController<User, ApiUser> {
    fun hello(user: ApiUser, request: Request): SOAResult<String> {
        return UserHelloService().execute(123, "HELLO", HashMap())
    }
}