package main.services.token

import framework.models.BaseIntEntity
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.Token

/**
 * Retrieve tokens based on a filtered type (value being the TokenType)
 */
class GetTokensService<T: Token>: SOAServiceInterface<T> {
    override fun execute(caller: Int?, key: String?, value: BaseIntEntity?) : SOAResult<List<T>> {
        throw NotImplementedError()
    }
}