package main.services.user_account

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.ApiCredNamespace
import java.security.KeyPairGenerator

/**
 * This service will be used to generate Api credentials
 */
class GenerateApiCredsService: SOAServiceInterface<ApiCredNamespace> {
    override fun execute() : SOAResult<ApiCredNamespace> {
        val key = KeyPairGenerator.getInstance("AES").genKeyPair()
        //TODO look into encryption
        return SOAResult(
                SOAResultType.SUCCESS,
                null,
                ApiCredNamespace(
                        key.public.toString(),
                        key.private.toString()
                )
        )
    }
}