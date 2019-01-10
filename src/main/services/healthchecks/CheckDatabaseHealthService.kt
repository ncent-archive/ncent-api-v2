package main.services.healthchecks

import kotlinserverless.framework.models.Handler
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType

object CheckDatabaseHealthService: SOAServiceInterface<Boolean> {
    override fun execute() : SOAResult<Boolean> {
        return try {
            Handler.connectToDatabase()
            SOAResult(SOAResultType.SUCCESS, "Successfully connected to database", true)
        } catch(e: Exception) {
            SOAResult(SOAResultType.SUCCESS, "Failed to connect to database: " + e.message, false)
        }
    }
}