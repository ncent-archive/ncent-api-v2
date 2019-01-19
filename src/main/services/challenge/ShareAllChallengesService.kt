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
            publicKey: String? = null,
            emailToShareWith: String? = null
    ) : SOAResult<Pair<TransactionList, NewUserAccount?>> {

        // Validate user exists or attempt to generate a new user.
        val getUserAccountResult = UserAccountHelper.getOrGenerateUser(emailToShareWith, publicKey)
        if(getUserAccountResult.result != SOAResultType.SUCCESS)
            return SOAResult(getUserAccountResult.result, getUserAccountResult.message)
        val publicKeyToShareWith = getUserAccountResult.data!!.first.cryptoKeyPair.publicKey
        val newUserAccount = getUserAccountResult.data!!.second

        // Pull list of non-transferred shares.
        val unsharedTransactions = GetUnsharedTransactionsService.execute(caller)
        if(unsharedTransactions.result != SOAResultType.SUCCESS)
            return SOAResult(SOAResultType.FAILURE, unsharedTransactions.message)

        // Iterate through unshared transactions and share all.
        var sharedTransactions = mutableListOf<Transaction>()
        unsharedTransactions.data!!.transactionsToShares.forEach {
            val challenge = Challenge.findById(it.transaction.action.data)!!
            val numShares = it.shares
            var result = ShareChallengeService.execute(
                    caller, challenge, numShares, publicKeyToShareWith, emailToShareWith)
            if(result.result != SOAResultType.SUCCESS)
                return SOAResult(SOAResultType.FAILURE, result.message)

            sharedTransactions.addAll(result.data!!.transactions)
        }

        return SOAResult(
                SOAResultType.SUCCESS,
                "Successfully transferred all challenge shares.",
                Pair(TransactionList(sharedTransactions), newUserAccount))
    }
}