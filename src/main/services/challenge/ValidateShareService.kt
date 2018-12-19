package main.services.challenge

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.ShareTransactionList

/**
 * Share a challenge.
 */
object ValidateShareService: SOAServiceInterface<Pair<Boolean, ShareTransactionList?>> {
    override fun execute(caller: Int?, params: Map<String, String>?) : SOAResult<Pair<Boolean, ShareTransactionList?>> {
        val unsharedTransactions = GetUnsharedTransactionsService.execute(
            caller,
            mapOf(Pair("challengeId", params!!["challengeId"]!!))
        )

        if(unsharedTransactions.result != SOAResultType.SUCCESS)
            throw Exception(unsharedTransactions.message)

        val availableShares = unsharedTransactions.data!!.transactionsToShares.map { it.second }.sum()

        val shares = if(params!!["shares"] != null) params!!["shares"]!!.toInt() else 0
        if(availableShares >= shares && availableShares != 0)
            return SOAResult(SOAResultType.SUCCESS, null, Pair(true,unsharedTransactions.data!!))
        return SOAResult(SOAResultType.SUCCESS, "You do not have enough shares available: $availableShares", Pair(false, null))
    }
}