package main.tokens.services

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.tokens.models.Token
import main.transactions.models.Transaction

/**
 * Transfer tokens from one address to another
 */
class TransferTokenService: SOAServiceInterface<Transaction> {
    override fun execute(caller: Int?, data: Transaction?, params: Map<String, String>?) : SOAResult<Transaction> {
        throw NotImplementedError()
    }
}