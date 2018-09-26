package main.transactions.services

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.transactions.models.Transaction

/**
 * Retrieve transactions by filter, such as from/to
 */
class GetTransactionsService<T>: SOAServiceInterface<Transaction<T>> {
    override fun execute(caller: Int?, params: Map<String, String>?) : SOAResult<List<Transaction<T>>> {
        throw NotImplementedError()
    }
}