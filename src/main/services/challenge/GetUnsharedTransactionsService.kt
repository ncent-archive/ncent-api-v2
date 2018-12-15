package main.services.challenge

import framework.models.idValue
import framework.services.DaoService
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.*
import main.services.transaction.GetTransactionsService

/**
 *
 * Retrieve one or more challenges based on filters
 *
 */
object GetUnsharedTransactionsService: SOAServiceInterface<ShareTransactionList> {
    // get challenges for a caller
    override fun execute(caller: Int?, params: Map<String, String>?): SOAResult<ShareTransactionList> {
        val transactionsToSharesResult = DaoService.execute {
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
                throw Exception(receivedTransactionResult.message)

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
                throw Exception(sharedTransactionResult.message)

            val receivedShares = receivedTransactionResult.data!!.transactions

            var sharedTransactionCount = mutableMapOf<Int, Int>()
            sharedTransactionResult.data!!.transactions.forEach { tx ->
                val shares = tx.metadatas.filter { it.key == "maxShares" }.first().value.toInt()
                tx.previousTransaction?.let {
                    val key = it.previousTransaction!!.idValue
                    val sharesPlusExistingShares = sharedTransactionCount.getOrDefault(key, 0) + shares
                    sharedTransactionCount[key] = sharesPlusExistingShares
                }
            }

            var unsharedTransactions = mutableListOf<Pair<Transaction, Int>>()

            receivedShares.forEach { receivedShare ->
                val shares = receivedShare.metadatas.filter { it.key == "maxShares" }.first().value.toInt()
                val availableShares = shares - sharedTransactionCount.getOrDefault(receivedShare.idValue, 0)
                if(availableShares > 0) {
                    unsharedTransactions.add(Pair(receivedShare, availableShares))
                }
            }

            return@execute unsharedTransactions
        }

        if(transactionsToSharesResult.result != SOAResultType.SUCCESS)
            return SOAResult(transactionsToSharesResult.result, transactionsToSharesResult.message, null)
        return SOAResult(SOAResultType.SUCCESS, null, ShareTransactionList(transactionsToSharesResult.data!!))
    }
}