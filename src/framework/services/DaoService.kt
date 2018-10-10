package framework.services

import kotlinserverless.framework.models.ApiGatewayResponse
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.SQLException

class DaoService<T> {
    fun execute(query: () -> T): SOAResult<T> {
        var result: SOAResult<T> = SOAResult(SOAResultType.FAILURE, "", null)
        try {
            val tx = transaction {
                addLogger(StdOutSqlLogger)
                return@transaction query()
            }
            result.data = tx
            result.result = SOAResultType.SUCCESS
        } catch(e: SQLException) {
            ApiGatewayResponse.LOG.error(e.message, e)
            println("There was a SQL error with a DaoService execution: " + e.message)
            result.message = e.message
        } catch(e: Exception) {
            ApiGatewayResponse.LOG.error(e.message, e)
            println("There was a general error with a DaoService execution: " + e.message)
            result.message = e.message
        } finally {
            return result
        }
    }
}