package kotlinserverless.main.controllers

import kotlinserverless.framework.controllers.DefaultController
import kotlinserverless.framework.controllers.RestController
import kotlinserverless.framework.models.*
import main.daos.Healthcheck
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.User

class HealthcheckController: DefaultController<Healthcheck>(), RestController<Healthcheck, User> {

    val elements: MutableList<Healthcheck> = ArrayList()

//    override fun findAll(user: ApiUser, filters: Map<String, Any>, pagination: Pagination): Page<Healthcheck> {
//        val offset = min(pagination.page * pagination.size, elements.size - 1)
//        val toIndex = min(offset + pagination.size, elements.size - 1)
//        elements.clear()
//        elements.add(Healthcheck("Healthy"))
//        return Page(filters, pagination.size, offset, elements.size, elements.subList(offset, toIndex))
//    }

    val defaultHealthyHealthCheck = Healthcheck.new {
        status = "Healthy"
        message = "default"
    }

    override fun findOne(user: User, id: Int): SOAResult<Healthcheck> {
        return SOAResult(SOAResultType.SUCCESS, "", defaultHealthyHealthCheck)
    }

    override fun findOne(user: User, filters: Map<String, Any>): SOAResult<Healthcheck> {
        return SOAResult(SOAResultType.SUCCESS, "", defaultHealthyHealthCheck)
    }

    override fun health(user: User, request: Request?): SOAResult<Healthcheck> {
        return SOAResult(SOAResultType.SUCCESS, "", defaultHealthyHealthCheck)
    }
}

