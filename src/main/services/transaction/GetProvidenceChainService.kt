package main.services.transaction

import framework.services.DaoService
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.*
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Retrieve a single providence chain.
 * The transaction queried must NOT have children and must be a leaf node
 *
 */
object GetProvidenceChainService: SOAServiceInterface<TransactionList> {
    override fun execute(caller: Int?, id: Int?): SOAResult<TransactionList> {
        // Get the transaction in question
        val txResult = GetTransactionService.execute(caller, id, null)
        // TODO -- verify that the transaction is a token type
        if (txResult.result != SOAResultType.SUCCESS)
            return SOAResult(txResult.result, txResult.message, null)
        var tx = txResult.data!!
        // verify no children exist
        var childrenResult = getChildren(tx.id)
        if (childrenResult.result != SOAResultType.SUCCESS)
            return SOAResult(childrenResult.result, childrenResult.message, null)
        // TODO -- check weights -- if children weight is less than parent weight; it's a valid chain still
        if (childrenResult.data!!.any())
            return SOAResult(SOAResultType.FAILURE, "Must send a leaf node, must not have children", null)

        val mainChain = getHistoricChain(tx)

        return SOAResult(SOAResultType.SUCCESS, null, TransactionList(mainChain))
    }

    fun getChildren(id: EntityID<Int>): SOAResult<List<Transaction>> {
        return DaoService.execute {
            Transaction.find {
                Transactions.previousTransaction eq id
            }?.distinct()?.toList()
        }
    }

    fun getHistoricChain(transaction: Transaction): MutableList<Transaction> {
        var tx = transaction
        // This will keep track of the original chain (tx of parents only)
        // Any transaction given will only have one chain of parents
        // And once you start factoring in children you will only then have multiple
        var mainChain = mutableListOf(tx)

        // Populate the list with its chain, going up the chain
        // Must put this in a transaction so we can access the deep objects
        transaction {
            while (tx.previousTransaction != null) {
                mainChain.add(tx.previousTransaction!!)
                tx = tx.previousTransaction!!
            }
        }

        // Reverse in order to put it in the proper descending (towards children) chain order
        mainChain.reverse()
        return mainChain
    }
}