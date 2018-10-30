package main.services.transaction

import framework.services.DaoService
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.Action
import main.daos.Transaction
import main.daos.TransactionNamespace
import main.daos.Transactions
import org.jetbrains.exposed.dao.EntityID

/**
 * Generate a transaction if it is valid
 */
class GenerateTransactionService: SOAServiceInterface<Transaction> {
    override fun execute(caller: Int?, d: Any?, params: Map<String, String>?) : SOAResult<Transaction> {
        val transactionNamespace = d!! as TransactionNamespace
        return DaoService<Transaction>().execute {
            val actionObj = Action.new {
                type = transactionNamespace.action.type
                data = transactionNamespace.action.data
                dataType = transactionNamespace.action.dataType
            }

            val previousTxEntity: EntityID<Int>? =
                    if(transactionNamespace.previousTransaction != null)
                        EntityID(transactionNamespace.previousTransaction, Transactions)
                    else
                        null
            return@execute Transaction.new {
                from = transactionNamespace.from
                to = transactionNamespace.to
                action = actionObj.id
                previousTransaction = previousTxEntity
            }
        }
    }
}