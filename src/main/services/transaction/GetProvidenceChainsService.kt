package main.services.transaction

import framework.models.idValue
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
class GetProvidenceChainsService: SOAServiceInterface<List<TransactionList>> {
    override fun execute(caller: Int?, id: Int?) : SOAResult<List<TransactionList>> {
        val txResult = GetTransactionService().execute(caller, id)
        if(txResult.result != SOAResultType.SUCCESS)
            return SOAResult(txResult.result, txResult.message, null)
        var tx = txResult.data!!
        var providenceChains = mutableMapOf<Int, List<Transaction>>()
        var providenceChainsFinal = mutableListOf<TransactionList>()
        var mainChain = mutableListOf(tx)
        while(tx.previousTransaction != null) {
            mainChain.add(tx.previousTransaction!!)
            tx = tx.previousTransaction!!
        }
        mainChain.reverse()

        providenceChains.put(mainChain.last().idValue, mainChain)

        while(providenceChains.any()) {
            val it = providenceChains.entries.first()
            var childrenResult = getChildren(it.value.last().id)
            var children = childrenResult.data
            while(childrenResult == SOAResultType.SUCCESS &&
                    children != null &&
                    children!!.any()) {
                if(children.count() > 1) {
                    // TODO make copies of the 'it' children.count() times - 1 (original)
                    // TODO push each child onto one of the new lists + include the original
                    // TODO use child id as the key
                    children.forEach {

                    }
                } else {
                    providenceChainsFinal.add(TransactionList(it.value))
                    providenceChains.remove(it.key)
                }
            }
        }
        return SOAResult(SOAResultType.SUCCESS, null, providenceChainsFinal)
    }

    fun getChildren(id: EntityID<Int>): SOAResult<List<Transaction>> {
        return DaoService<List<Transaction>>().execute {
            Transaction.find {
                Transactions.previousTransaction eq id
            }.distinct().toList()
        }
    }
}