package main.services.transaction

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.TransactionList

/**
 * Retrieve transactions by filter, such as from/to
 */
class GetTransactionsService: SOAServiceInterface<TransactionList> {
    override fun execute(caller: Int?, params: Map<String, String>?) : SOAResult<TransactionList> {
        throw NotImplementedError()
    }
}