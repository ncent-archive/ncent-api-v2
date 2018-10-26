package main.services.user_account

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.Session

/**
 * Used to end a session (cache)
 */
class EndSessionService: SOAServiceInterface<Session> {
    override fun execute(caller: Int?, key: String?) : SOAResult<Session> {
        throw NotImplementedError()
    }
}