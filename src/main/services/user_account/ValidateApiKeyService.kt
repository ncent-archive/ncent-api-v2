package main.services.user_account

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.UserAccount

/**
 * Validate the accuracy of the passed ApiKey and Secret key in the UserAccount
 */
class ValidateApiKeyService: SOAServiceInterface<UserAccount> {
    override fun execute(caller: Int?, data: Any?, params: Map<String, String>?) : SOAResult<UserAccount> {
        throw NotImplementedError()
    }
}