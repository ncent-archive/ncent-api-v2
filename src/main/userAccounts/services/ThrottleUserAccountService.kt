package main.userAccounts.services

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.userAccounts.models.UserAccount

/**
 * Check if we need to throttle the user
 */
class ThrottleUserAccountService: SOAServiceInterface<UserAccount> {
    override fun execute(caller: Int?, data: UserAccount?, params: Map<String, String>?) : SOAResult<UserAccount> {
        throw NotImplementedError()
    }
}