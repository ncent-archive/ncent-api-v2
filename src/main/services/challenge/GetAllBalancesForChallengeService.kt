package main.services.challenge

import framework.models.idValue
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import main.helpers.ChallengeHelper
import main.helpers.UserAccountHelper
import main.services.transaction.GetTransactionsService

object GetAllBalancesForChallengeService {
    fun execute(caller: UserAccount, challengeId: Int): SOAResult<EmailToChallengeBalanceList> {
        val allBalancesForChallenge = EmailToChallengeBalanceList(challengeId, mutableMapOf())

        val challenge = ChallengeHelper.findChallengeById(challengeId)
        if (caller.idValue != challenge.challengeSettings.admin.idValue) {
            return SOAResult(SOAResultType.FAILURE,"User not permitted to make this call")
        }

        val actionNamespace = ActionNamespace(ActionType.SHARE, challengeId, "Challenge")
        val shareTransactionsResult = GetTransactionsService.execute(null, null, null, actionNamespace)
        if(shareTransactionsResult.result != SOAResultType.SUCCESS)
            return SOAResult(SOAResultType.FAILURE, shareTransactionsResult.message)

        if(shareTransactionsResult.data != null && shareTransactionsResult.data?.transactions?.isEmpty() == true)
            return SOAResult(SOAResultType.FAILURE, "No share transactions for this challenge")

        val publicKeys = shareTransactionsResult.data!!.transactions.map { it -> it.to }.distinct()
        val userAccounts = publicKeys.map { it -> UserAccountHelper.getOrGenerateUser(null, it).data }

        for (userAccount in userAccounts) {
            allBalancesForChallenge.publicKeyToChallengeBalances.put(userAccount!!.first.userMetadata.email, getChallengeBalanceForUser(userAccount.first, challengeId))
        }

        return SOAResult(SOAResultType.SUCCESS, null, allBalancesForChallenge)
    }

    private fun getChallengeBalanceForUser(userAccount: UserAccount, challengeId: Int): Int {
        var balance = 0
        val unsharedTransactionList = GetUnsharedTransactionsService.execute(userAccount, challengeId).data
        for (transactionToShare in unsharedTransactionList!!.transactionsToShares) {
            balance += transactionToShare.second
        }

        return balance
    }
}