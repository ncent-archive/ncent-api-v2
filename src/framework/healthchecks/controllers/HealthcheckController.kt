package kotlinserverless.framework.healthchecks.controllers

import kotlinserverless.framework.controllers.DefaultController
import kotlinserverless.framework.healthchecks.models.Healthcheck
import kotlinserverless.framework.Request
import kotlinserverless.framework.healthchecks.services.HealthcheckService

class HealthcheckController : DefaultController<Healthcheck> {
	fun <T> execute(request: Request?) : Any? {
        return defaultRouting(Healthcheck::class.java, request!!, HealthcheckService())
    }
}