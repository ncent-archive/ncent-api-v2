package framework.services

import kotlinserverless.framework.models.*
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
            Handler.log(e, e.message)
            println("There was service error: " + e.message)
            result.message = if(e.message != null) e.message else e.toString()
        } catch(e: SoAFailureException) {
            Handler.log(e, e.message)
            println("There was service failure: " + e.message)
            result.message = if(e.message != null) e.message else e.toString()
        } catch(e: SQLException) {
            Handler.log(e, e.message)
            println("There was a SQL error with a DaoService execution: " + e.message)
            result.message = if(e.message != null) e.message else e.toString()
        } catch(e: Throwable) {
            Handler.log(e, e.message)
            println("There was a general error with a DaoService execution: " + e.message)
            result.message = if(e.message != null) e.message else e.toString()
        } finally {
            return result
        }
    }

    fun throwOrReturn(result: SOAResult<*>) {
        when {
            result.message?.equals("Invalid api credentials") == true -> {
                throw UnauthorizedError(result.message!!)
            }
            result.message?.contains("Cannot transition from") == true ->  {
                throw ForbiddenException(result.message)
            }
            result.message?.contains("User not permitted") == true ->  {
                throw ForbiddenException(result.message)
            }
            result.message?.contains("You do not have enough") == true ->  {
                throw ForbiddenException(result.message)
            }
            result.message?.contains("has not been") == true ->  {
                throw ForbiddenException(result.message)
            }
            result.message?.contains("user cannot") == true -> {
                throw ForbiddenException(result.message)
            }
            else -> null
        }

        when(result.result) {
            SOAResultType.FAILURE -> throw SoAFailureException(result.message)
            SOAResultType.ERROR -> throw SoAErrorException(result.message)
            else -> null
        }

        if(result.data is SOAResult<*>)
            throwOrReturn(result.data!! as SOAResult<*>)
    }
}