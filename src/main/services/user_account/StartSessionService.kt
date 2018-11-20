package main.services.user_account

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.SessionNamespace
import org.joda.time.DateTime
import java.util.*

/**
 * Used to start a session (login cache)
 */
class StartSessionService: SOAServiceInterface<SessionNamespace> {
    override fun execute() : SOAResult<SessionNamespace> {
        //TODO look into encryption
        return SOAResult(
            SOAResultType.SUCCESS,
            null,
            SessionNamespace(
                UUID.randomUUID().toString(),
                DateTime.now().plusHours(24)
            )
        )
    }
}