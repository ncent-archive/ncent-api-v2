package main.services.transaction

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.select

/**
 * Retrieve a transaction and it's connecting objects
 */
object GetTransactionService {
    fun execute(transactionId: Int) : SOAResult<Transaction> {
        val query = Transactions
            .innerJoin(Actions)
            .innerJoin(TransactionsMetadata)
            .innerJoin(Metadatas)
        .select {
            Transactions.id eq EntityID(transactionId, Transactions)
        }.withDistinct()
        val tx = Transaction.wrapRows(query).toList().distinct()
        return SOAResult(SOAResultType.SUCCESS, null, tx.first())
    }
}