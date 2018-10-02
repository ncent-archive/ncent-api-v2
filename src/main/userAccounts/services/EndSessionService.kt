package main.userAccounts.services

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.userAccounts.models.UserAccount

/**
 * Used to end a session (cache)
 */
class EndSessionService: SOAServiceInterface<UserAccount> {
    override fun execute() : SOAResult<UserAccount> {
        throw NotImplementedError()
    }
}