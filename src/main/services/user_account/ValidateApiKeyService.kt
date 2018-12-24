package main.services.user_account

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.ApiCred
import main.daos.ApiCreds
import main.helpers.EncryptionHelper

/**
 * Validate the accuracy of the passed ApiKey and Secret key in the UserAccount
 */
object ValidateApiKeyService: SOAServiceInterface<ApiCred> {
    override fun execute(caller: Int?, data: Any?, params: Map<String, String>?) : SOAResult<ApiCred> {
        var result = SOAResult<ApiCred>(
            SOAResultType.FAILURE,
            "",
            null
        )
        val apiKeyParam = params!!["apiKey"]!!
        val secretKey = params!!["secretKey"]!!
        val apiCred = ApiCred.find {
            ApiCreds.apiKey eq apiKeyParam
        }
        if(apiCred.empty() ||
            !EncryptionHelper.validateEncryption(
                secretKey,
                apiCred.first()._secretKeySalt,
                apiCred.first()._secretKey)) {
            result.message = "Invalid api credentials"
        } else {
            result.data = apiCred.first()
            result.result = SOAResultType.SUCCESS
        }
        return result
    }
}