package main.services.user_account

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.CryptoKeyPair
import org.stellar.sdk.KeyPair

/**
 * This service will be used to generate crypto keypair
 */
object GenerateCryptoKeyPairService: SOAServiceInterface<CryptoKeyPair> {
    override fun execute() : SOAResult<CryptoKeyPair> {
        val key = KeyPair.random()
        return SOAResult(SOAResultType.SUCCESS, null, CryptoKeyPair.new {
            publicKey = key.publicKey.toString()
            encryptedPrivateKey = CryptoKeyPair.encryptPrivateKey(key.secretSeed.toString())
        })
        //TODO look into encryption
    }
}