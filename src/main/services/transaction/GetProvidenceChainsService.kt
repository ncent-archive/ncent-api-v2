package main.services.transaction

import framework.models.idValue
import framework.services.DaoService
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.*
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

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
        // Get the transaction in question
        val txResult = GetTransactionService().execute(caller, id, null)
        if(txResult.result != SOAResultType.SUCCESS)
            return SOAResult(txResult.result, txResult.message, null)
        var tx = txResult.data!!
        // Keep a map of all providence chains that we are populating
        var providenceChains = mutableMapOf<Int, MutableList<Transaction>>()
        // Once completed populating (no more children), a chain is moved to this list
        var providenceChainsFinal = mutableListOf<TransactionList>()

        // This will keep track of the original chain (tx of parents only)
        // Any transaction given will only have one chain of parents
        // And once you start factoring in children you will only then have multiple
        var mainChain = mutableListOf(tx)

        // Populate the list with its chain, going up the chain
        // Must put this in a transaction so we can access the deep objects
        transaction {
            while(tx.previousTransaction != null) {
                mainChain.add(tx.previousTransaction!!)
                tx = tx.previousTransaction!!
            }
        }

        // Reverse in order to put it in the proper descending (towards children) chain order
        mainChain.reverse()

        // Add the main chain to the temporary map of 'all chains that are in progress'
        providenceChains[mainChain.last().idValue] = mainChain
        // While the map has anything in it, we need to continue populating and reducing it
        while(providenceChains.any()) {

            // Grab any of the providence chains to work on
            var providenceChain = providenceChains.entries.first()

            val currentLastIdInChain = providenceChain.value.last().id

            var childrenResult = getChildren(currentLastIdInChain)

            if(childrenResult.result != SOAResultType.SUCCESS)
                return SOAResult(childrenResult.result, childrenResult.message, null)

            var children = childrenResult.data

            // if this chain has no children, we can just finalize it
            if(children == null || children.isEmpty()) {
                providenceChainsFinal.add(TransactionList(providenceChain.value))
                providenceChains.remove(providenceChain.key)
                continue
            }

            // create a temp copy of the chain
            // if there are more than 1 children, we will need to copy the chain
            // for each additional child; we can use the temp to maintain the main copy
            var providenceChainTemp = mutableListOf<Transaction>()
            if(children.count() > 1)
                providenceChain.value.forEach { t -> providenceChainTemp.add(t) }

            // Loop through the children so we can create new chains
            children.forEachIndexed { index, child ->
                // If we are on the first iteration, we can just add the child to the end
                if(index == 0) {
                    providenceChain.value.add(child)
                } else {
                    var providenceChainNew = mutableListOf<Transaction>()
                    providenceChainTemp.forEach { t -> providenceChainNew.add(t) }
                    providenceChainNew.add(child)
                    providenceChains[child.idValue] = providenceChainNew
                }
            }
        }
        return SOAResult(SOAResultType.SUCCESS, null, providenceChainsFinal)
    }

    private fun getChildren(id: EntityID<Int>): SOAResult<List<Transaction>> {
        return DaoService<List<Transaction>>().execute {
            Transaction.find {
                Transactions.previousTransaction eq id
            }?.distinct()?.toList()
        }
    }
}