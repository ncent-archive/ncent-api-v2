package main.tokens.services

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.tokens.models.Token
import main.transactions.models.Transaction

/**
 * Transfer tokens from one address to another
 */
class TransferTokenService: SOAServiceInterface<Transaction<Token>> {
    override fun execute(caller: Int?, data: Transaction<Token>?, params: Map<String, String>?) : SOAResult<Transaction<Token>> {
        throw NotImplementedError()
    }
}