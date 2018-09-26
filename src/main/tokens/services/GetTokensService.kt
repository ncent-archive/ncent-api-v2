package main.tokens.services

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.tokens.models.Token
import kotlinserverless.framework.models.BaseModel

/**
 * Retrieve tokens based on a filtered type (value being the TokenType)
 */
class GetTokensService: SOAServiceInterface<Token> {
    override fun execute(caller: Int?, key: String?, value: BaseModel?) : SOAResult<List<Token>> {
        throw NotImplementedError()
    }
}