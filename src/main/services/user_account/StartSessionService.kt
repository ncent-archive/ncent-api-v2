package main.services.user_account

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.Session
import main.daos.UserAccount

/**
 * Used to start a session (login cache)
 */
class StartSessionService: SOAServiceInterface<Session> {
    override fun execute(caller: Int?, key: String?, value: String?) : SOAResult<Session> {
        throw NotImplementedError()
    }
}