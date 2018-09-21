package kotlinserverless.main.users.services

import kotlinserverless.framework.models.*
import kotlinserverless.framework.services.Service
import kotlinserverless.main.users.models.User

class UserService: Service<User, ApiUser> {
	fun hello(user: ApiUser? = null, request: Request? = null): String {
		return "HELLO WORLD"
	}
}