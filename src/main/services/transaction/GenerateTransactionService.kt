package main.services.transaction

import framework.services.DaoService
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.*
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.SizedCollection

/**
 * Generate a transaction if it is valid
 */
class GenerateTransactionService: SOAServiceInterface<Transaction> {
    override fun execute(caller: Int?, d: Any?, params: Map<String, String>?) : SOAResult<Transaction> {
        val transactionNamespace = d!! as TransactionNamespace
        return DaoService<Transaction>().execute {
            val actionObj = Action.new {
                type = transactionNamespace.action!!.type
                data = transactionNamespace.action!!.data
                dataType = transactionNamespace.action!!.dataType
            }

            val previousTxEntity: EntityID<Int>? =
                    if (transactionNamespace.previousTransaction != null)
                        EntityID(transactionNamespace.previousTransaction, Transactions)
                    else
                        null

            return@execute Transaction.new {
                from = transactionNamespace.from
                to = transactionNamespace.to
                action = actionObj.id
                previousTransaction = previousTxEntity
                metadatas = SizedCollection(listOf())
            }
        }
    }
}