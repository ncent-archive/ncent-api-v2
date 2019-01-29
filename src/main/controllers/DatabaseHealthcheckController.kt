package main.controllers

import framework.services.DaoService
import kotlinserverless.framework.controllers.DefaultController
import kotlinserverless.framework.controllers.RestController
import kotlinserverless.framework.models.Handler
import main.daos.Healthcheck
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.User
import main.daos.UserAccount
import main.helpers.ControllerHelper.RequestData
import main.services.healthchecks.CheckDatabaseHealthService

class DatabaseHealthcheckController: DefaultController<Healthcheck>(), RestController<Healthcheck, UserAccount> {
    override fun health(user: UserAccount?, requestData: RequestData): SOAResult<Healthcheck> {
        val dbResult = DaoService.execute {
            val isDatabaseHealthyCheck = CheckDatabaseHealthService.execute()
            if (isDatabaseHealthyCheck.result != SOAResultType.SUCCESS) {
                return@execute SOAResult(SOAResultType.FAILURE, "Failed to connect", null)
            }
            val result: SOAResult<Healthcheck> = SOAResult(
                SOAResultType.SUCCESS,
                isDatabaseHealthyCheck.message,
                null
            )
            if(isDatabaseHealthyCheck.data!!) {
                result.data = Healthcheck.findByStatus("database_healthy")
            } else {
                result.data = Healthcheck.findByStatus("database_unhealthy")
            }
            return@execute result
        }.data!!
        return SOAResult(dbResult.result, dbResult.message, dbResult.data)
    }
}

