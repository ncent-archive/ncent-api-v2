package main.tokens.services

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.tokens.models.Token

/**
 * Generate a token if it is valid
 */
class GenerateTokenService: SOAServiceInterface<Token> {
    override fun execute(caller: Int?, data: Token?, params: Map<String, String>?) : SOAResult<Token> {
        throw NotImplementedError()
    }
}