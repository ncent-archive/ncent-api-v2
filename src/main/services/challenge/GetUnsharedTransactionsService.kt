package main.services.challenge

import framework.models.idValue
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.*
import main.services.transaction.GetTransactionsService
import org.joda.time.DateTime

/**
 *
 * Retrieve one or more challenges based on filters
 *
 */
object GetUnsharedTransactionsService: SOAServiceInterface<ShareTransactionList> {
    // get challenges for a caller
    override fun execute(caller: Int?, params: Map<String, String>?): SOAResult<ShareTransactionList> {
        val publicKey = UserAccount.findById(caller!!)!!.cryptoKeyPair.publicKey
        val receivedTransactionResult = GetTransactionsService.execute(
            caller,
            mapOf(
                Pair("to", publicKey),
                Pair("dataType", "Challenge"),
                Pair("data", params!!["challengeId"]!!),
                Pair("type", "SHARE")
            )
        )

        if(receivedTransactionResult.result != SOAResultType.SUCCESS)
            return SOAResult(SOAResultType.FAILURE, receivedTransactionResult.message)

        val sharedTransactionResult = GetTransactionsService.execute(
            caller,
            mapOf(
                Pair("from", publicKey),
                Pair("dataType", "Challenge"),
                Pair("data", params!!["challengeId"]!!),
                Pair("type", "SHARE")
            )
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