package main.services.challenge

import framework.models.idValue
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import main.services.transaction.GetTransactionsService
import org.joda.time.DateTime

/**
 *
 * Retrieve one or more challenges based on filters
 *
 */
object GetUnsharedTransactionsService {
    // get challenges for a caller
    fun execute(caller: UserAccount, challengeId: Int): SOAResult<ShareTransactionList> {
        val publicKey = caller.cryptoKeyPair.publicKey
        val receivedTransactionResult = GetTransactionsService.execute(
            null,
            publicKey,
            null,
            ActionNamespace(ActionType.SHARE, challengeId, "Challenge")
        )

        if(receivedTransactionResult.result != SOAResultType.SUCCESS)
            return SOAResult(SOAResultType.FAILURE, receivedTransactionResult.message)

        val sharedTransactionResult = GetTransactionsService.execute(
            publicKey,
            null,
            null,
            ActionNamespace(ActionType.SHARE, challengeId, "Challenge")
        )

        if(sharedTransactionResult.result != SOAResultType.SUCCESS)
            return SOAResult(SOAResultType.FAILURE, sharedTransactionResult.message)

        val receivedShares = receivedTransactionResult.data!!.transactions

        var sharedTransactionCount = mutableMapOf<Int, Int>()
        sharedTransactionResult.data!!.transactions.forEach { tx ->
            val shares = tx.metadatas.filter { it.key == "maxShares" }.first().value.toInt()
            if(hasShareExpired(tx, Transaction.find { Transactions.previousTransaction eq tx.id }.count())) return@forEach
            tx.previousTransaction?.let { prevTx ->
                val sharesPlusExistingShares = sharedTransactionCount.getOrDefault(prevTx.idValue, 0) + shares
                sharedTransactionCount[prevTx.idValue] = sharesPlusExistingShares
            }
        }

        var unsharedTransactions = mutableListOf<Pair<Transaction, Int>>()

        receivedShares.forEach { receivedShare ->
            val shares = receivedShare.metadatas.filter { it.key == "maxShares" }.first().value.toInt()
            val availableShares = shares - sharedTransactionCount.getOrDefault(receivedShare.idValue, 0)
            if(availableShares > 0 && !hasShareExpired(receivedShare, sharedTransactionCount.get(receivedShare.idValue))) {
                unsharedTransactions.add(Pair(receivedShare, availableShares))
            }
        }

        return SOAResult(SOAResultType.SUCCESS, null, ShareTransactionList(unsharedTransactions))
    }

    private fun hasShareExpired(receivedShare: Transaction, shared: Int?): Boolean {
        if(DateTime.parse(receivedShare.metadatas.filter { it.key == "shareExpiration" }.first().value).isBeforeNow && shared == null) {
            return true
        }
        return false
    }
}