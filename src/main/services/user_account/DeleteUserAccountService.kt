package main.services.user_account

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.*

/**
 * This service will be used to generate a full User Account
 */
object DeleteUserAccountService {
    fun execute(user: UserAccount) : SOAResult<Boolean> {
        user.userMetadata.delete()
        user.cryptoKeyPair.delete()
        user.apiCreds.delete()
        user.session.delete()
        return SOAResult(SOAResultType.SUCCESS, null, null)
    }
}
