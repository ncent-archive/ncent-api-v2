package main.services.transaction

import framework.services.DaoService
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.*

/**
 * Retrieve transactions by filter'd action
 * Specifically to be used to get a providence chain
 * Can do so by filtering for a specific action data and dataType
 */
class GetTransactionChainService: SOAServiceInterface<TransactionList> {
    override fun execute(caller: Int?, params: Map<String, String>?) : SOAResult<TransactionList> {
        if(params!!["data"] == null || params!!["dataType"] == null)
            return SOAResult(SOAResultType.FAILURE, "data and dataType are required.", null)
        val actionsResult = DaoService<List<Action>>().execute {
            Action.find {
                Actions.data eq Integer.valueOf(params!!["data"]!!)
                Actions.dataType eq params!!["dataType"]!!
            }.distinct()
        }
        if(actionsResult.result != SOAResultType.SUCCESS)
            return SOAResult(actionsResult.result, actionsResult.message, null)
        val actions = actionsResult.data!!

        val transactionsResult = DaoService<List<Transaction>>().execute {
            Transaction.find {
                Transactions.action inList actions.map { a -> a.id }
            }.distinct()
        }

        // TODO may need to transform the transactions list returned to only
        // include a subset of the transactions.

        if(transactionsResult.result == SOAResultType.SUCCESS)
            return SOAResult(SOAResultType.SUCCESS, null, TransactionList(transactionsResult.data!!.distinct()))
        return SOAResult(transactionsResult.result, transactionsResult.message, null)
    }
}