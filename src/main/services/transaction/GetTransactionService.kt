package main.services.transaction

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.*
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.select

/**
 * Retrieve a transaction and it's connecting objects
 */
object GetTransactionService: SOAServiceInterface<Transaction> {
    override fun execute(caller: Int?, id: Int?, params: Map<String, String>?) : SOAResult<Transaction> {
        val query = Transactions
            .innerJoin(Actions)
            .innerJoin(TransactionsMetadata)
            .innerJoin(Metadatas)
        .select {
            Transactions.id eq EntityID(id!!, Transactions)
        }.withDistinct()
        val tx = Transaction.wrapRows(query).toList().distinct()
        return SOAResult(SOAResultType.SUCCESS, null, tx.first())
    }
}