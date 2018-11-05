package main.services.transaction

import framework.services.DaoService
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.*
import org.jetbrains.exposed.sql.select

/**
 * Retrieve a transaction and it's connecting objects
 */
class GetTransactionService: SOAServiceInterface<TransactionList> {
    override fun execute(caller: Int?, id: Int?, params: Map<String, String>?) : SOAResult<TransactionList> {
        val transactionsResult = DaoService<List<Transaction>>().execute {
            val query = Transactions
                .innerJoin(Actions)
                .innerJoin(Transactions)
                .innerJoin(Metadatas)
            .select {
                Transactions.id eq id!!
            }.withDistinct()
            Transaction.wrapRows(query).toList()
        }

        if(transactionsResult.result != SOAResultType.SUCCESS)
            return SOAResult(transactionsResult.result, transactionsResult.message, null)
        return SOAResult(SOAResultType.SUCCESS, null, TransactionList(transactionsResult.data!!))
    }
}