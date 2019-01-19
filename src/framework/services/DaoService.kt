package framework.services

import kotlinserverless.framework.models.ApiGatewayResponse
import kotlinserverless.framework.models.SoAErrorException
import kotlinserverless.framework.models.SoAFailureException
import kotlinserverless.framework.models.UnauthorizedError
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.SQLException

object DaoService {
    fun <T> execute(query: () -> T): SOAResult<T> {
        var result: SOAResult<T> = SOAResult(SOAResultType.FAILURE, "", null)
        try {
            val tx = transaction {
                // TODO figure out why logger is repeating
                // TODO this slows down execution by 4x
                //addLogger(StdOutSqlLogger)
                return@transaction query()
            }
            result.data = tx
            result.result = SOAResultType.SUCCESS
        } catch(e: SoAErrorException) {
            ApiGatewayResponse.LOG.error(e.message, e)
            println("There was service error: " + e.message)
            result.message = if(e.message != null) e.message else e.toString()
        } catch(e: SoAFailureException) {
            ApiGatewayResponse.LOG.error(e.message, e)
            println("There was service failure: " + e.message)
            result.message = if(e.message != null) e.message else e.toString()
        } catch(e: SQLException) {
            ApiGatewayResponse.LOG.error(e.message, e)
            println("There was a SQL error with a DaoService execution: " + e.message)
            result.message = if(e.message != null) e.message else e.toString()
        } catch(e: Throwable) {
            ApiGatewayResponse.LOG.error(e.message, e)
            println("There was a general error with a DaoService execution: " + e.message)
            result.message = if(e.message != null) e.message else e.toString()
        } finally {
            return result
        }
    }

    fun throwOrReturn(result: SOAResultType, message: String?) {
        if(message?.equals("Invalid api credentials") == true)
            throw UnauthorizedError(message!!)

        when(result) {
            SOAResultType.FAILURE -> throw SoAFailureException(message)
            SOAResultType.ERROR -> throw SoAErrorException(message)
            else -> return
        }
    }
}