package main.services.transaction

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.Transaction

/**
 * Retrieve a transaction and it's connecting objects
 */
class GetTransactionService: SOAServiceInterface<Transaction> {
    override fun execute(caller: Int?, id: Int?, params: Map<String, String>?) : SOAResult<Transaction> {
        throw NotImplementedError()
    }
}