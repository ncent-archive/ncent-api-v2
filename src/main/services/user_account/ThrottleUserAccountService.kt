package main.services.user_account

import kotlinserverless.framework.services.SOAResult
import main.daos.UserAccount

/**
 * Check if we need to throttle the user
 * We should use a cache to keep track of requests attempted each second/minute/hour
 * We should throttle users based on a hard cap
 * We should allow whitelists for specific users
 * We should allow editable limits for some as well
 */
object ThrottleUserAccountService {
    fun execute(caller: UserAccount) : SOAResult<UserAccount> {
        throw NotImplementedError()
    }
}