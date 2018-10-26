package main.services.user_account

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.UserAccount

/**
 * This service will be used to generate a full User Account
 */
class GenerateUserAccountService: SOAServiceInterface<UserAccount> {
    override fun execute(caller: Int?, data: UserAccount?, params: Map<String, String>?) : SOAResult<UserAccount> {
        throw NotImplementedError()
    }
}