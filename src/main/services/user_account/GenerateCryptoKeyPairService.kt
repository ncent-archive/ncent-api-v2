package main.services.user_account

import framework.services.DaoService
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.CryptoKeyPair
import main.daos.CryptoKeyPairNamespace
import org.stellar.sdk.KeyPair

/**
 * This service will be used to generate crypto keypair
 */
object GenerateCryptoKeyPairService: SOAServiceInterface<CryptoKeyPair> {
    override fun execute() : SOAResult<CryptoKeyPair> {
        val key = KeyPair.random()
        return DaoService.execute {
            CryptoKeyPair.new {
                publicKey = key.publicKey.toString()
                encryptedPrivateKey = CryptoKeyPair.encryptPrivateKey(key.secretSeed.toString())
            }
        }
        //TODO look into encryption
    }
}