package main.services.user_account

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.Session
import main.daos.Sessions
import org.joda.time.DateTime
import org.joda.time.DateTimeZone

/**
 * Used to end a session (cache)
 */
object EndSessionService {
    fun execute(key: String) : SOAResult<Session> {
        val result = SOAResult<Session>(SOAResultType.FAILURE, null, null)

        var sessionQuery = Session.find { Sessions.sessionKey eq key }

        if (sessionQuery.empty()) {
            result.message = "session not found"
            return result
        }

        val session = sessionQuery.first()
        result.result = SOAResultType.SUCCESS

        if(session.expiration > DateTime.now(DateTimeZone.UTC)) {
            session.expiration = DateTime.now(DateTimeZone.UTC)
        }

        result.data = session
        return result
    }
}