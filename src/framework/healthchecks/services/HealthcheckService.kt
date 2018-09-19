package kotlinserverless.framework.healthchecks.services

import kotlin.math.min
import kotlinserverless.framework.models.*
import kotlinserverless.framework.services.Service
import kotlinserverless.framework.healthchecks.models.Healthcheck
import kotlinserverless.framework.healthchecks.InvalidEndpoint

class HealthcheckService: Service<Healthcheck, ApiUser> {
	
	val elements: MutableList<Healthcheck> = ArrayList()
	
	override fun create(user: ApiUser, element: Healthcheck) {
		throw InvalidEndpoint()
	}
	
	override fun findAll(user: ApiUser, filters: Map<String, Any>, pagination: Pagination): Page<Healthcheck> {
		val offset = min(pagination.page * pagination.size, elements.size - 1)
        val toIndex = min(offset + pagination.size, elements.size - 1)
		elements.clear()
		elements.add(Healthcheck("Healthy"))
		return Page(filters, pagination.size, offset, elements.size, elements.subList(offset, toIndex))
	}
	
	override fun findOne(user: ApiUser, id: Int): Healthcheck {
		return Healthcheck("Healthy")
	}
	
	override fun findOne(user: ApiUser, filters: Map<String, Any>): Healthcheck {
        return Healthcheck("Healthy")
    }
	
	override fun update(user: ApiUser, element: Healthcheck) {
		throw InvalidEndpoint()
	}
	
	override fun delete(user: ApiUser, id: Int) {
		throw InvalidEndpoint()
	}

    override fun count(user: ApiUser, filters: Map<String, Any>): Int {
        throw InvalidEndpoint()
    }

    override fun exists(user: ApiUser, id: Int): Boolean {
        throw InvalidEndpoint()
    }
	
	override fun health(): Healthcheck {
		return Healthcheck("Healthy")
	}
}

