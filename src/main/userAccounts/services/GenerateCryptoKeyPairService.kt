package main.userAccounts.services

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.userAccounts.models.CryptoKeyPair

/**
 * This service will be used to generate crypto keypair
 */
class GenerateCryptoKeyPairService: SOAServiceInterface<CryptoKeyPair> {
    override fun execute() : SOAResult<CryptoKeyPair> {
        throw NotImplementedError()
    }
}