package main.tokens.services

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.tokens.models.TokenType

/**
 * Generate a token type if it is valid
 */
class GenerateTokenTypeService: SOAServiceInterface<TokenType> {
    override fun execute(caller: Int?, data: TokenType?, params: Map<String, String>?) : SOAResult<TokenType> {
        throw NotImplementedError()
    }
}