package main.services.challenge

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import main.helpers.UserAccountHelper

/**
 * Share all challenges from a user's account.
 */
object ShareAllChallengesService {
    fun execute(
            caller: UserAccount,
            publicKeyToShareWith: String? = null,
            emailToShareWith: String? = null
    ) : SOAResult<Pair<TransactionList, NewUserAccount?>> {

        // Validate user exists or attempt to generate a new user.
        val publicKeyAndAccount = UserAccountHelper.getOrGenerateUser(emailToShareWith, publicKeyToShareWith)
        val(publicKeyToShareWith, newUserAccount) =
                if(publicKeyAndAccount.result != SOAResultType.SUCCESS)
                    return SOAResult(publicKeyAndAccount.result, publicKeyAndAccount.message)
                else
                    publicKeyAndAccount.data!!

        // Pull list of non-transferred shares.
        val unsharedTransactions = GetUnsharedTransactionsService.execute(caller)
        if(unsharedTransactions.result != SOAResultType.SUCCESS)
            return SOAResult(SOAResultType.FAILURE, unsharedTransactions.message)

        // Iterate through unshared transactions and share all.
        var sharedTransactions = mutableListOf<Transaction>()
        unsharedTransactions.data!!.transactionsToShares.forEach {
            val challenge = Challenge.findById(it.first.action.data)!!
            var result = ShareChallengeService.execute(
                    caller, challenge, it.second, publicKeyToShareWith, emailToShareWith)
            if(result.result != SOAResultType.SUCCESS)
                return SOAResult(SOAResultType.FAILURE, result.message, result.data)

            sharedTransactions.addAll(result.data!!.first.transactions)
        }

        return SOAResult(
                SOAResultType.SUCCESS,
                "Successfully transferred all challenge shares.",
                Pair(TransactionList(sharedTransactions), publicKeyAndAccount.data!!.second))
    }
}