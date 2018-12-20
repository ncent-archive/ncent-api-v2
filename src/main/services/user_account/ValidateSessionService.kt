package main.services.user_account

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.Session
import main.daos.UserAccount
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.joda.time.DateTimeZone

/**
 * Validate the session
 */
object ValidateSessionService: SOAServiceInterface<Session> {
    override fun execute(caller: Int?, key: String?) : SOAResult<Session> {
        var result = SOAResult<Session>(
            SOAResultType.FAILURE,
            "",
            null
        )
        val user = UserAccount.findById(caller!!)!!
        val session = user.session
        if(session == null) {
            result.message = "Session not found"
        } else if(session.sessionKey != key!!) {
            result.message = "Invalid Session"
        } else if(session!!.expiration <= DateTime.now(DateTimeZone.UTC)) {
            result.message = "Session Expired"
        } else {
            result.data = session
            result.result = SOAResultType.SUCCESS
        }
        return result
    }
}