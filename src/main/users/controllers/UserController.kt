package main.users.controllers

import kotlinserverless.framework.controllers.RestController
import kotlinserverless.framework.models.ApiUser
import kotlinserverless.framework.models.Request
import kotlinserverless.main.users.models.User
import kotlinserverless.main.users.services.UserHelloService

class UserController: RestController<User, ApiUser> {
    fun hello(user: ApiUser, request: Request): String? {
        return UserHelloService().execute(123, "HELLO", HashMap()).data
    }
}