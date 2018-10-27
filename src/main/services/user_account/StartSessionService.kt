package main.services.user_account

import io.kotlintest.hours
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.SessionNamespace
import org.joda.time.DateTime
import java.security.KeyPairGenerator

/**
 * Used to start a session (login cache)
 */
class StartSessionService: SOAServiceInterface<SessionNamespace> {
    override fun execute(caller: Int?, key: String?, value: String?) : SOAResult<SessionNamespace> {
        val key = KeyPairGenerator.getInstance("AES").generateKeyPair()
        //TODO look into encryption
        return SOAResult(
                SOAResultType.SUCCESS,
                null,
                SessionNamespace(
                    key.public.toString(),
                    DateTime.now().plusHours(24)
                )
        )
    }
}