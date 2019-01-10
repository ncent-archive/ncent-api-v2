package main.services.user_account

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import main.helpers.EncryptionHelper

/**
 * Validate the accuracy of the passed crypto pub/priv in the UserAccount
 */
object ValidateCryptoKeyPairService {
    fun execute(publicKey: String, privateKey: String) : SOAResult<CryptoKeyPair> {
        var result = SOAResult<CryptoKeyPair>(SOAResultType.FAILURE, null, null)
        val cryptoKeyPair = CryptoKeyPair.find {
            CryptoKeyPairs.publicKey eq publicKey
        }
        if(cryptoKeyPair.empty() ||
            !EncryptionHelper.validateEncryption(
                privateKey,
                cryptoKeyPair.first()._privateKeySalt,
                cryptoKeyPair.first()._privateKey)) {
            result.message = "Invalid key pair"
        } else {
            result.data = cryptoKeyPair.first()
            result.result = SOAResultType.SUCCESS
        }
        return result
    }
}