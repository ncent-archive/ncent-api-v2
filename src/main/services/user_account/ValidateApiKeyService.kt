package main.services.user_account

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.ApiCred
import main.daos.ApiCreds
import main.daos.UserAccount
import main.helpers.EncryptionHelper

/**
 * Validate the accuracy of the passed ApiKey and Secret key in the UserAccount
 */
object ValidateApiKeyService {
    fun execute(caller: UserAccount, secretKey: String) : SOAResult<ApiCred> {
        var result = SOAResult<ApiCred>(SOAResultType.FAILURE, null, null)
        val apiCred = caller.apiCreds
        if(!EncryptionHelper.validateEncryption(
                secretKey,
                apiCred._secretKeySalt,
                apiCred._secretKey)) {
            result.message = "Invalid api credentials"
        } else {
            result.data = apiCred
            result.result = SOAResultType.SUCCESS
        }
        return result
    }
}