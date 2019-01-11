package main.services.challenge

import framework.models.idValue
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import main.services.completion_criteria.ValidateCompletionCriteriaService
import main.services.reward.DistributeRewardService

/**
 * Trigger a challenge state change to active.
 */
object CompleteChallengeService {
    fun execute(caller: UserAccount, params: Map<String, String>?) : SOAResult<TransactionList> {
        val challenge = Challenge.findById(params!!["challengeId"]!!.toInt())!!

        // check if the completion criteria matches.
        val validationResult = ValidateCompletionCriteriaService.execute(
            caller,
            challenge.completionCriterias
        )
        if(validationResult.result != SOAResultType.SUCCESS)
            return SOAResult(SOAResultType.FAILURE, validationResult.message)

        // validate we can do the state transition
        val newState = ActionType.valueOf("COMPLETE")
        val oldTx = challenge.getLastStateChangeTransaction()!!
        val oldState = oldTx.action.type
        if(!challenge.canTransitionState(oldState, newState))
            return SOAResult(SOAResultType.FAILURE, "Cannot transition from ${oldState.type} to ${newState.type}")

        // validate user has any shares available -- without any they cannot complete
        val completingUserPublicKey = params!!["completingUserPublicKey"]!!
        val completingKeyPair = CryptoKeyPair.find { CryptoKeyPairs.publicKey eq completingUserPublicKey }.first()
        val userToCompleteWith = UserAccount.find {
            UserAccounts.cryptoKeyPair eq completingKeyPair.idValue
        }.first()

        val sharabilityResult = ValidateShareService.execute(userToCompleteWith, challenge)

        if(sharabilityResult.result != SOAResultType.SUCCESS)
            return SOAResult(SOAResultType.FAILURE, sharabilityResult.message)

        if(!sharabilityResult.data!!.first)
            return SOAResult(SOAResultType.FAILURE, "User must have a share in order to complete")

        val unsharedTransactions = sharabilityResult.data!!.second!!

        // transition state
        val stateChangeResult = ChangeChallengeStateService.execute(caller, challenge.idValue, ActionType.COMPLETE)
        if(stateChangeResult.result != SOAResultType.SUCCESS)
            return SOAResult(SOAResultType.FAILURE, stateChangeResult.message)

        // decide which transaction to use ( TODO for now the first unshared tx?)
        val firstUnspentTx = unsharedTransactions.transactionsToShares.first().first.idValue

        // TODO: generate a tx moving the share to the completion contract

        // payout winner
        return DistributeRewardService.execute(
            challenge.completionCriterias.reward.idValue,
            firstUnspentTx
        )
    }
}