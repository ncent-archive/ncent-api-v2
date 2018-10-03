package kotlinserverless.framework.healthchecks.controllers

import kotlinserverless.framework.controllers.DefaultController
import kotlinserverless.framework.controllers.RestController
import kotlin.math.min
import kotlinserverless.framework.models.*
import kotlinserverless.framework.healthchecks.models.Healthcheck

class HealthcheckController: DefaultController<Healthcheck>(), RestController<Healthcheck, ApiUser> {

    val elements: MutableList<Healthcheck> = ArrayList()

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

    override fun health(user: ApiUser, request: Request?): Healthcheck {
        return Healthcheck("Healthy")
    }
}

