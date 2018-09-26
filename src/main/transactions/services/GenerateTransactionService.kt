package main.transactions.services

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.transactions.models.Transaction

/**
 * Generate a transaction if it is valid
 */
class GenerateTransactionService<T>: SOAServiceInterface<Transaction<T>> {
    override fun execute(caller: Int?, data: Transaction<T>?, params: Map<String, String>?) : SOAResult<Transaction<T>> {
        throw NotImplementedError()
    }
}