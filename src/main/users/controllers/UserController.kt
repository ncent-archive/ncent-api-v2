package kotlinserverless.main.users.controllers

import kotlinserverless.framework.controllers.DefaultController
import kotlinserverless.framework.Request
import kotlinserverless.main.users.models.User
import kotlinserverless.main.users.services.UserService

class UserController : DefaultController<User> {
	fun <T> execute(request: Request?) : Any? {
		return defaultRouting(User::class.java, request!!, UserService())
    }
}