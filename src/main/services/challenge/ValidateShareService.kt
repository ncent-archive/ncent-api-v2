package main.services.challenge

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.ShareTransactionList
import main.daos.UserAccount

/**
 * Share a challenge.
 */
object ValidateShareService: SOAServiceInterface<Pair<Boolean, ShareTransactionList?>> {
    override fun execute(caller: UserAccount, params: Map<String, String>?) : SOAResult<Pair<Boolean, ShareTransactionList?>> {
        val unsharedTransactions = GetUnsharedTransactionsService.execute(
            caller,
            params = mapOf(Pair("challengeId", params!!["challengeId"]!!))
        )

        if(unsharedTransactions.result != SOAResultType.SUCCESS)
            return SOAResult(SOAResultType.FAILURE, unsharedTransactions.message)

        val availableShares = unsharedTransactions.data!!.transactionsToShares.map { it.second }.sum()

        val shares = if(params!!["shares"] != null) params!!["shares"]!!.toInt() else 0
        if(availableShares >= shares && availableShares != 0)
            return SOAResult(SOAResultType.SUCCESS, null, Pair(true,unsharedTransactions.data!!))
        return SOAResult(SOAResultType.SUCCESS, "You do not have enough valid shares available: $availableShares", Pair(false, null))
    }
}