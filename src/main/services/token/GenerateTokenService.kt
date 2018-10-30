package main.services.token

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.Token

/**
 * Generate a token if it is valid
 */
class GenerateTokenService: SOAServiceInterface<Token> {
    override fun execute(caller: Int?, data: Any?, params: Map<String, String>?) : SOAResult<Token> {
        throw NotImplementedError()
    }
}