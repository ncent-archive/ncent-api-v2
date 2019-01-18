package main.controllers

import kotlinserverless.framework.controllers.DefaultController
import kotlinserverless.framework.controllers.RestController
import main.daos.Healthcheck
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.User
import main.daos.UserAccount
import main.helpers.ControllerHelper.RequestData
import main.services.healthchecks.CheckDatabaseHealthService

class HealthcheckController: DefaultController<Healthcheck>(), RestController<Healthcheck, UserAccount> {
    override fun health(user: UserAccount, requestData: RequestData): SOAResult<Healthcheck> {
        val isDatabaseHealthyCheck = CheckDatabaseHealthService.execute()
        if (isDatabaseHealthyCheck.result == SOAResultType.FAILURE) {
            return SOAResult(SOAResultType.FAILURE, "Failed to connect", null)
        }
        val result: SOAResult<Healthcheck> = SOAResult(
            SOAResultType.SUCCESS,
            isDatabaseHealthyCheck.message,
            null
        )
        if(isDatabaseHealthyCheck.data!!) {
            result.data = Healthcheck.findByStatus("healthy")
        } else {
            result.data = Healthcheck.findByStatus("unhealthy")
        }
        return result
    }
}

