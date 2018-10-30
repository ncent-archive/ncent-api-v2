package main.services.token

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.TokenType

/**
 * Generate a token type if it is valid
 */
class GenerateTokenTypeService: SOAServiceInterface<TokenType> {
    override fun execute(caller: Int?, data: Any?, params: Map<String, String>?) : SOAResult<TokenType> {
        throw NotImplementedError()
    }
}