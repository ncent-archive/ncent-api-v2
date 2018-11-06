package main.services.transaction

import framework.models.idValue
import framework.services.DaoService
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.*
import org.jetbrains.exposed.dao.EntityID
import java.util.*

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
        var providenceChains = mutableMapOf<Int, MutableList<Transaction>>()
        var providenceChainsFinal = mutableListOf<TransactionList>()
        var mainChain = mutableListOf(tx)
        while(tx.previousTransaction != null) {
            mainChain.add(tx.previousTransaction!!)
            tx = tx.previousTransaction!!
        }
        mainChain.reverse()

        providenceChains[mainChain.last().idValue] = mainChain

        while(providenceChains.any()) {
            var providenceChain = providenceChains.entries.first()
            val currentLastIdInChain = providenceChain.value.last().id
            var childrenResult = getChildren(currentLastIdInChain)
            var children = childrenResult.data
            while(childrenResult == SOAResultType.SUCCESS &&
                    children != null &&
                    children!!.any()) {
                if(children.count() > 1) {
                    children.forEach { child ->
                        if(providenceChain.value.last().id != currentLastIdInChain) {
                            var providenceChainNew = mutableListOf<Transaction>()
                            Collections.copy(providenceChainNew, providenceChain.value)
                            providenceChainNew.add(child)
                            providenceChains[child.idValue] = providenceChainNew
                        } else {
                            providenceChain.value.add(child)
                        }
                    }
                } else {
                    providenceChainsFinal.add(TransactionList(providenceChain.value))
                    providenceChains.remove(providenceChain.key)
                }
            }
        }
        return SOAResult(SOAResultType.SUCCESS, null, providenceChainsFinal)
    }

    private fun getChildren(id: EntityID<Int>): SOAResult<List<Transaction>> {
        return DaoService<List<Transaction>>().execute {
            Transaction.find {
                Transactions.previousTransaction eq id
            }.distinct().toList()
        }
    }
}