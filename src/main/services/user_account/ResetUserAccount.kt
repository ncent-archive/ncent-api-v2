package main.services.user_account

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.*

/**
 * This service is used reset a user's API key and crypto key pair.
 */
object ResetUserAccount {
    fun execute(userAccount: UserAccount) : SOAResult<NewUserAccount> {
        // Reset the user's API keys
        val updatedApiKeys = ResetApiCredsService.execute(userAccount)
        if (updatedApiKeys.result != SOAResultType.SUCCESS) {
            return SOAResult(updatedApiKeys.result, updatedApiKeys.message, null)
        }

        // Reset the user's crypto keypair
        val updatedCryptoKeyPair = ResetCryptoKeyPairService.execute(userAccount)
        if (updatedCryptoKeyPair.result != SOAResultType.SUCCESS) {
            return SOAResult(updatedCryptoKeyPair.result, updatedCryptoKeyPair.message, null)
        }

        val newUserAccount = NewUserAccount(
                userAccount,
                updatedCryptoKeyPair.data!!.secret,
                updatedApiKeys.data!!.secretKey)

        return SOAResult(
                SOAResultType.SUCCESS,
                "Successfully reset user's API key and crypto keypair.",
                newUserAccount)
    }
}
