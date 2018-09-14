package kotlinserverless.main.users.services

import kotlinserverless.framework.*
import kotlinserverless.framework.services.Service
import kotlinserverless.framework.healthchecks.models.Healthcheck
import kotlinserverless.main.users.models.User
import kotlinserverless.framework.ApiUser

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

	override fun health(): Healthcheck {
		throw NotImplementedError()
	}
	
	fun hello(user: ApiUser? = null, request: Request? = null): String {
		return "HELLO WORLD"
	}
}