package main.services.user_account

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.UserAccount

/**
 * Used to start a session (login cache)
 */
class StartSessionService: SOAServiceInterface<UserAccount> {
    override fun execute() : SOAResult<UserAccount> {
        throw NotImplementedError()
    }
}