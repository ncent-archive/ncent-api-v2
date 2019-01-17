package main.services.challenge

import framework.models.idValue
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import main.helpers.UserAccountHelper
import main.services.transaction.GenerateTransactionService
import main.services.transaction.GetTransactionsService
import main.services.user_account.GenerateUserAccountService
import org.jetbrains.exposed.sql.select
import org.joda.time.DateTime

/**
 * Share a challenge.
 */
object ShareChallengeService {
    fun execute(
        caller: UserAccount,
        challenge: Challenge,
        shares: Int,
        publicKeyToShareWith: String? = null,
        emailToShareWith: String? = null,
        expiration: String? = null
    ) : SOAResult<TransactionWithNewUser> {

        // Validate user exists or attempt to generate a new user
        val publicKeyAndAccount = UserAccountHelper.getOrGenerateUser(emailToShareWith, publicKeyToShareWith)
        val(publicKeyToShareWith, newUserAccount) =
            if(publicKeyAndAccount.result != SOAResultType.SUCCESS)
                return SOAResult(publicKeyAndAccount.result, publicKeyAndAccount.message)
            else
                publicKeyAndAccount.data!!

        // TODO look into using a datetime formatter
        val expiration = expiration ?: challenge.challengeSettings.shareExpiration.toString()

        return if(challenge.challengeSettings.offChain)
            shareOffChain(caller, challenge, shares, publicKeyToShareWith, expiration, newUserAccount)
        else
            shareOnChain(caller, challenge, shares, publicKeyToShareWith, expiration, newUserAccount)
    }

    private fun shareOnChain(
        caller: UserAccount,
        challenge: Challenge,
        shares: Int,
        publicKeyToShareWith: String,
        expiration: String,
        newUserAccount: NewUserAccount?
    ): SOAResult<TransactionWithNewUser> {

        // validate the user has enough shares
        // check that we can share based on # shares available and attempt
        val sharabilityResult = ValidateShareService.execute(caller, challenge, shares)

        if(sharabilityResult.result != SOAResultType.SUCCESS || !sharabilityResult.data!!.first)
            return SOAResult(SOAResultType.FAILURE, sharabilityResult.message)

        val unsharedTransactions = sharabilityResult.data!!.second!!

        // loop sharing new tx until all spent that is needed
        // TODO look into partial share -- if one tx fails what happens
        var txs = mutableListOf<Transaction>()
        var shared = 0
        unsharedTransactions.transactionsToShares.forEach {
            if(shared >= shares)
                return@forEach
            val ustx = it.first
            val amount = it.second

            val txResult = GenerateTransactionService.execute(TransactionNamespace(
                from = caller.cryptoKeyPair.publicKey,
                to = publicKeyToShareWith,
                previousTransaction = ustx.idValue,
                metadatas = ChallengeMetadata(
                                challenge.idValue,
                                challenge.challengeSettings.offChain,
                                expiration,
                                Math.min(shares, amount)
                            ).getChallengeMetadataNamespaces().toTypedArray(),
                action = ActionNamespace(
                    type = ActionType.SHARE,
                    data = challenge.idValue,
                    dataType = Challenge::class.simpleName!!
                )
            ))

            // TODO decide what to do if the txresult fails
            txs.add(txResult.data!!)
            shared += amount
        }
        return SOAResult(SOAResultType.SUCCESS, null, TransactionWithNewUser(txs, newUserAccount))
    }

    private fun shareOffChain(
        caller: UserAccount,
        challenge: Challenge,
        shares: Int,
        publicKeyToShareWith: String,
        expiration: String,
        newUserAccount: NewUserAccount?
    ): SOAResult<TransactionWithNewUser> {
        // create a tx using previous as the first tx
        val receivedTransactionResult = GetTransactionsService.execute(
            null,
            caller.cryptoKeyPair.publicKey,
            null,
            ActionNamespace(ActionType.SHARE, challenge.idValue, "Challenge")
        )

        if(receivedTransactionResult.result != SOAResultType.SUCCESS)
            return SOAResult(SOAResultType.FAILURE, receivedTransactionResult.message)

        val previousTx = receivedTransactionResult.data!!.transactions?.first()

        val txResult = GenerateTransactionService.execute(TransactionNamespace(
            from = caller.cryptoKeyPair.publicKey,
            to = publicKeyToShareWith,
            previousTransaction = previousTx?.idValue,
            metadatas = ChallengeMetadata(
                            challenge.idValue,
                            challenge.challengeSettings.offChain,
                            expiration,
                            shares
                        ).getChallengeMetadataNamespaces().toTypedArray(),
            action = ActionNamespace(
                type = ActionType.SHARE,
                data = challenge.idValue,
                dataType = Challenge::class.simpleName!!
            )
        ))

        if(txResult.result != SOAResultType.SUCCESS)
            return SOAResult(SOAResultType.FAILURE, txResult.message)
        return SOAResult(SOAResultType.SUCCESS, null, TransactionWithNewUser(listOf(txResult.data!!), newUserAccount))
    }
}