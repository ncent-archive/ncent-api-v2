package main.services.challenge

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import main.helpers.ChallengeHelper
import main.services.transaction.GetTransactionsService

object GetAllBalancesForChallengeService {
    fun execute(caller: UserAccount, challengeId: Int): SOAResult<PublicKeyToChallengeBalanceList> {
        val allBalancesForChallenge = PublicKeyToChallengeBalanceList(challengeId, mutableMapOf())

        val challenge = ChallengeHelper.findChallengeById(challengeId)
        val actionNamespace = ActionNamespace(ActionType.SHARE, challengeId, "Challenge")
        val shareTransactionsResult = GetTransactionsService.execute(null, null, null, actionNamespace)
        if(shareTransactionsResult.result != SOAResultType.SUCCESS)
            return SOAResult(SOAResultType.FAILURE, shareTransactionsResult.message)

        if(shareTransactionsResult.data != null && shareTransactionsResult.data?.transactions?.isEmpty() == true)
            return SOAResult(SOAResultType.FAILURE, "No share transactions for this challenge")

        val publicKeys = shareTransactionsResult.data!!.transactions.map { it -> it.to }

    }
}