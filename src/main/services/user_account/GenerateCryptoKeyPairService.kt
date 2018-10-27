package main.services.user_account

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.CryptoKeyPairNamespace
import org.stellar.sdk.KeyPair

/**
 * This service will be used to generate crypto keypair
 */
class GenerateCryptoKeyPairService: SOAServiceInterface<CryptoKeyPairNamespace> {
    override fun execute() : SOAResult<CryptoKeyPairNamespace> {
        val key = KeyPair.random()
        //TODO look into encryption
        return SOAResult(
                SOAResultType.SUCCESS,
                null,
                CryptoKeyPairNamespace(
                    key.publicKey.toString(),
                    key.secretSeed.toString()
                )
        )
    }
}