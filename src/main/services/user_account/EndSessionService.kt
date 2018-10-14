package main.services.user_account

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.UserAccount

/**
 * Used to end a session (cache)
 */
class EndSessionService: SOAServiceInterface<UserAccount> {
    override fun execute() : SOAResult<UserAccount> {
        throw NotImplementedError()
    }
}