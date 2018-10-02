package main.userAccounts.services

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.userAccounts.models.UserAccount

/**
 * Used to start a session (login cache)
 */
class StartSessionService: SOAServiceInterface<UserAccount> {
    override fun execute() : SOAResult<UserAccount> {
        throw NotImplementedError()
    }
}