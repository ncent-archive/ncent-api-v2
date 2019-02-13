package main.services.healthchecks

import kotlinserverless.framework.models.Handler
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType

object CheckDatabaseHealthService {
    fun execute() : SOAResult<Boolean> {
        return try {
            Handler.connectToDatabase()
            SOAResult(SOAResultType.SUCCESS, "Successfully connected to database", true)
        } catch(e: Exception) {
            Handler.log(e, "Failed to connect to database")
            SOAResult(SOAResultType.FAILURE, "Failed to connect to database: " + e.message, false)
        }
    }
}