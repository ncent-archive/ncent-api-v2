package main.services.transaction

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import org.jetbrains.exposed.sql.SizedCollection

/**
 * Generate a transaction if it is valid
 */
object GenerateTransactionService {
    fun execute(transactionNamespace: TransactionNamespace) : SOAResult<Transaction> {
        val actionObj = Action.new {
            type = transactionNamespace.action!!.type!!
            data = transactionNamespace.action!!.data!!
            dataType = transactionNamespace.action!!.dataType
        }

        val previousTxEntity: Transaction? =
            if (transactionNamespace.previousTransaction != null)
                Transaction.findById(transactionNamespace.previousTransaction!!.toInt())
            else
                null

        val metadatasToAdd = if(transactionNamespace.metadatas != null) {
            transactionNamespace.metadatas.map {
                md -> Metadata.new {
                    key = md.key
                    value = md.value
                }
            }
        } else {
            listOf()
        }

        var transaction =  Transaction.new {
            from = transactionNamespace.from!!
            to = transactionNamespace.to
            action = actionObj
            previousTransaction = previousTxEntity
        }

        transaction.metadatas = SizedCollection(metadatasToAdd)
        return SOAResult(SOAResultType.SUCCESS, null, transaction)
    }
}