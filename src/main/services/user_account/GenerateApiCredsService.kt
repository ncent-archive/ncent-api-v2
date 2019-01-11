package main.services.user_account

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.ApiCredNamespace
import org.stellar.sdk.KeyPair

/**
 * This service will be used to generate Api credentials
 */
object GenerateApiCredsService {
    fun execute() : SOAResult<ApiCredNamespace> {
        //TODO figure out best practices for apikey/secret key generation
        val key = KeyPair.random()
        //TODO look into encryption
        return SOAResult(
                SOAResultType.SUCCESS,
                null,
                ApiCredNamespace(
                    key.publicKey.toString(),
                    key.secretSeed.toString()
                )
        )
    }
}