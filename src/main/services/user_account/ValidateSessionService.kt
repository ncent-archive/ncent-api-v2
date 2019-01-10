package main.services.user_account

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.Session
import main.daos.UserAccount
import org.joda.time.DateTime
import org.joda.time.DateTimeZone

/**
 * Validate the session
 */
object ValidateSessionService {
    fun execute(caller: UserAccount, key: String) : SOAResult<Session> {
        var result = SOAResult<Session>(SOAResultType.FAILURE, null, null)
        val session = caller.session
        if(session == null) {
            result.message = "Session not found"
        } else if(session.sessionKey != key) {
            result.message = "Invalid Session"
        } else if(session.expiration <= DateTime.now(DateTimeZone.UTC)) {
            result.message = "Session Expired"
        } else {
            result.data = session
            result.result = SOAResultType.SUCCESS
        }
        return result
    }
}