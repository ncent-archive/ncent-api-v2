package main.services.user_account

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.SessionNamespace
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import java.util.*

/**
 * Used to start a session (login cache)
 */
object StartSessionService {
    fun execute() : SOAResult<SessionNamespace> {
        //TODO look into encryption
        return SOAResult(
            SOAResultType.SUCCESS,
            null,
            SessionNamespace(
                UUID.randomUUID().toString(),
                DateTime.now(DateTimeZone.UTC).plusHours(24).toString()
            )
        )
    }
}