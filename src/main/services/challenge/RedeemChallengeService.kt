package main.services.challenge

import framework.models.idValue
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import main.services.completion_criteria.ValidateCompletionCriteriaService
import main.services.reward.DistributeRewardService
import main.services.transaction.GenerateTransactionService

/**
 * Redeem a challenge to complete a single providence chain
 */
object RedeemChallengeService {
    fun execute(caller: UserAccount, challenge: Challenge, completerPublicKey: String) : SOAResult<TransactionList> {
        // TODO -- we should check for the caller if they should be calling CompleteChallengeService instead
        // TODO ^ -- if the max completers has been met by this call we should complete the challenge
        // check if the completion criteria matches.
        val oldTx = challenge.getLastStateChangeTransaction()!!
        if (oldTx.action.type != ActionType.ACTIVATE) {
            return SOAResult(SOAResultType.FAILURE, "Challenge has not been activated")
        }

        val validationResult = ValidateCompletionCriteriaService.execute(
            caller,
            challenge.completionCriterias
        )
        if(validationResult.result != SOAResultType.SUCCESS)
            return SOAResult(SOAResultType.FAILURE, validationResult.message)

        // validate user has any shares available -- without any they cannot complete
        val completingKeyPair = CryptoKeyPair.find { CryptoKeyPairs.publicKey eq completerPublicKey }.first()
        val userToCompleteWith = UserAccount.find {
            UserAccounts.cryptoKeyPair eq completingKeyPair.idValue
        }.first()

        val sharabilityResult = ValidateShareService.execute(userToCompleteWith, challenge)

        if(sharabilityResult.result != SOAResultType.SUCCESS)
            return SOAResult(SOAResultType.FAILURE, sharabilityResult.message)

        if(!sharabilityResult.data!!.first)
            return SOAResult(SOAResultType.FAILURE, "User must have a share in order to complete")

        val unsharedTransactions = sharabilityResult.data!!.second!!

        // decide which transaction to use ( TODO for now the first unshared tx?)
        val firstUnspentTx = unsharedTransactions.transactionsToShares.first().first

        // TODO test this gets generated correctly
        // generate a tx moving the share to the completion contract so it cannot be used again
        val txResult = GenerateTransactionService.execute(TransactionNamespace(
            from = completerPublicKey,
            to = challenge.completionCriterias.address,
            previousTransaction = firstUnspentTx.idValue,
            metadatas = ChallengeMetadata(
                            challenge.idValue,
                            challenge.challengeSettings.offChain,
                            challenge.challengeSettings.shareExpiration.toString(),
                            1
                        ).getChallengeMetadataNamespaces().toTypedArray(),
            action = ActionNamespace(
                type = ActionType.SHARE,
                data = challenge.idValue,
                dataType = Challenge::class.simpleName!!
            )
        ))

        if(txResult.result != SOAResultType.SUCCESS)
            return SOAResult(SOAResultType.FAILURE, txResult.message)

        // payout winner
        return DistributeRewardService.execute(
            challenge.completionCriterias.reward,
            firstUnspentTx
        )
    }
}