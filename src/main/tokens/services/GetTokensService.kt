package main.tokens.services

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.tokens.models.Token

/**
 * Retrieve tokens based on a filter (ex: id, type)
 */
class GetTokensService: SOAServiceInterface<Token> {
    override fun execute(caller: Int?, params: Map<String, String>?) : SOAResult<List<Token>> {
        throw NotImplementedError()
    }
}a