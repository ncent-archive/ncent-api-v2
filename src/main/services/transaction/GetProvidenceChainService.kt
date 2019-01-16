package main.services.transaction

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import org.jetbrains.exposed.dao.EntityID

/**
 * Retrieve a single providence chain.
 * The transaction queried must NOT have children and must be a leaf node
 *
 */
object GetProvidenceChainService {
    fun execute(transaction: Transaction): SOAResult<TransactionList> {
        // TODO -- verify that the transaction is a token type
        // verify no children exist
        // TODO we may not need this check
//        var childrenResult = getChildren(tx.id)
//        if (childrenResult.result != SOAResultType.SUCCESS)
//            return SOAResult(childrenResult.result, childrenResult.message, null)
//        // TODO -- check weights -- if children weight is less than parent weight; it's a valid chain still
//        if (childrenResult.data!!.any())
//            return SOAResult(SOAResultType.FAILURE, "Must send a leaf node, must not have children", null)

        val mainChain = getHistoricChain(transaction)

        return SOAResult(SOAResultType.SUCCESS, null, TransactionList(mainChain))
    }

    fun getChildren(id: EntityID<Int>): List<Transaction> {
        return Transaction.find {
            Transactions.previousTransaction eq id
        }?.distinct()?.toList()
    }

    fun getHistoricChain(transaction: Transaction): MutableList<Transaction> {
        var tx = transaction
        // This will keep track of the original chain (tx of parents only)
        // Any transaction given will only have one chain of parents
        // And once you start factoring in children you will only then have multiple
        var mainChain = mutableListOf(tx)

        // Populate the list with its chain, going up the chain
        // Must put this in a transaction so we can access the deep objects
        while (tx.previousTransaction != null) {
            mainChain.add(tx.previousTransaction!!)
            tx = tx.previousTransaction!!
        }

        // Reverse in order to put it in the proper descending (towards children) chain order
        mainChain.reverse()
        return mainChain
    }
}