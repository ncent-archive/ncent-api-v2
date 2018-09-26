package main.transactions.services

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.transactions.models.Transaction

/**
 * Retrieve a transaction and it's connecting objects
 */
class GetTransactionService<T>: SOAServiceInterface<Transaction<T>> {
    override fun execute(caller: Int?, id: Int?, params: Map<String, String>?) : SOAResult<Transaction<T>> {
        throw NotImplementedError()
    }
}