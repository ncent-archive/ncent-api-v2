package kotlinserverless.main.users.services

import kotlinserverless.framework.models.*
import kotlinserverless.framework.services.Service
import kotlinserverless.framework.healthchecks.models.Healthcheck
import kotlinserverless.main.users.models.User

class UserService: Service<User, ApiUser> {

    override fun create(user: ApiUser, element: User) {
		throw NotImplementedError()
	}

    override fun findAll(user: ApiUser, filters: Map<String, Any>, pagination: Pagination): Page<User> {
		throw NotImplementedError()
	}

    override fun findOne(user: ApiUser, id: Int): User {
		throw NotImplementedError()
	}

    override fun findOne(user: ApiUser, filters: Map<String, Any>): User {
		throw NotImplementedError()
	}

    override fun update(user: ApiUser, element: User) {
		throw NotImplementedError()
	}

    override fun delete(user: ApiUser, id: Int) {
		throw NotImplementedError()
	}

    override fun count(user: ApiUser, filters: Map<String, Any>): Int {
		throw NotImplementedError()
	}

    override fun exists(user: ApiUser, id: Int): Boolean {
		throw NotImplementedError()
	}

	override fun health(user: ApiUser, request: Request?): Healthcheck {
		throw NotImplementedError()
	}
	
	fun hello(user: ApiUser? = null, request: Request? = null): String {
		return "HELLO WORLD"
	}
}