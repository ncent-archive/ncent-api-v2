package main.services.challenge

import framework.models.idValue
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.Challenge
import main.daos.ShareTransactionList
import main.daos.UserAccount

/**
 * Share a challenge.
 */
object ValidateShareService {
    fun execute(caller: UserAccount, challenge: Challenge, shares: Int = 0) : SOAResult<Pair<Boolean, ShareTransactionList?>> {
        val unsharedTransactions = GetUnsharedTransactionsService.execute(
            caller,
            challenge.idValue
        )

        if(unsharedTransactions.result != SOAResultType.SUCCESS)
            return SOAResult(SOAResultType.FAILURE, unsharedTransactions.message)

        val availableShares = unsharedTransactions.data!!.transactionsToShares.map { it.second }.sum()

        if(availableShares >= shares && availableShares != 0)
            return SOAResult(SOAResultType.SUCCESS, null, Pair(true,unsharedTransactions.data!!))
        return SOAResult(SOAResultType.SUCCESS, "You do not have enough valid shares available: $availableShares", Pair(false, null))
    }
}