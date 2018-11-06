package main.services.transaction

import framework.services.DaoService
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.*
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.alias
import org.jetbrains.exposed.sql.leftJoin
import org.jetbrains.exposed.sql.select

/**
 * Retrieve a transaction and it's connecting objects
 */
class GetTransactionService: SOAServiceInterface<TransactionList> {
    override fun execute(caller: Int?, id: Int?, params: Map<String, String>?) : SOAResult<TransactionList> {
        val transactionsResult = DaoService<List<Transaction>>().execute {
            val prevTxTable = Transactions.alias("previous_tx")
            val query = Transactions
                .innerJoin(Actions)
                .leftJoin(prevTxTable, { Transactions.id }, {prevTxTable[Transactions.previousTransaction]})
                .innerJoin(TransactionsMetadata)
                .innerJoin(Metadatas)
            .select {
                Transactions.id eq EntityID(id!!, Transactions)
            }.withDistinct()
            Transaction.wrapRows(query).toList().distinct()
        }

        if(transactionsResult.result != SOAResultType.SUCCESS)
            return SOAResult(transactionsResult.result, transactionsResult.message, null)
        return SOAResult(SOAResultType.SUCCESS, null, TransactionList(transactionsResult.data!!))
    }
}