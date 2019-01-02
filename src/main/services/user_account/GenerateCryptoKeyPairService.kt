package main.services.user_account

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.CryptoKeyPair
import main.daos.NewCryptoKeyPair
import org.stellar.sdk.KeyPair

/**
 * This service will be used to generate crypto keypair
 */
object GenerateCryptoKeyPairService: SOAServiceInterface<NewCryptoKeyPair> {
    override fun execute() : SOAResult<NewCryptoKeyPair> {
        val key = KeyPair.random()
        val secret = key.secretSeed.toString()
        return SOAResult(
            SOAResultType.SUCCESS,
            null,
            NewCryptoKeyPair(
                CryptoKeyPair.new {
                    publicKey = key.publicKey.toString()
                    privateKey = secret
                },
                secret
            )
        )
    }
}