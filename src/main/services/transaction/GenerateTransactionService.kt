package main.services.transaction

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.Transaction

/**
 * Generate a transaction if it is valid
 */
class GenerateTransactionService: SOAServiceInterface<Transaction> {
    override fun execute(caller: Int?, data: Transaction?, params: Map<String, String>?) : SOAResult<Transaction> {
        throw NotImplementedError()
    }
}