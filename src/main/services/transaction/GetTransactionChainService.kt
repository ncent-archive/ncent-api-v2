package main.services.transaction

import framework.services.DaoService
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.*
import org.jetbrains.exposed.dao.EntityID

/**
 * Retrieve transactions by filter'd action
 * Specifically to be used to get a providence chain
 * Can do so by filtering for a specific action data and dataType
 * Must be a challenge action type
 * May return multiple chains in the case that a transaction
 * Has multiple routes as children
 *
 */
class GetTransactionChainService: SOAServiceInterface<TransactionList> {
    override fun execute(caller: Int?, id: Int?) : SOAResult<TransactionList> {
        val txResult = GetTransactionService().execute(caller, id)
        if(txResult.result != SOAResultType.SUCCESS)
            return SOAResult(txResult.result, txResult.message, null)
        var tx = txResult.data!!
        var providenceChains = mutableListOf<List<Transaction>>()
        var mainChain = mutableListOf(tx)
        while(tx.previousTransaction != null) {
            mainChain.add(tx.previousTransaction!!)
            tx = tx.previousTransaction!!
        }
        mainChain.reverse()

        var childrenResult = getChildren(mainChain.last().id)
        var children = childrenResult.data
        while(childrenResult == SOAResultType.SUCCESS &&
                children != null &&
                children!!.any()) {
            if(children.count() > 1) {

            } else {
                
            }
        }
    }

    fun getChildren(id: EntityID<Int>): SOAResult<List<Transaction>> {
        return DaoService<List<Transaction>>().execute {
            Transaction.find {
                Transactions.previousTransaction eq id
            }.distinct().toList()
        }
    }
}