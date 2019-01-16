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
    fun execute(caller: UserAccount, challenge: Challenge, completerPublicKey: String) : SOAResult<TransactionList> {
        // validate we can do the state transition
        val newState = ActionType.valueOf("COMPLETE")
        val oldTx = challenge.getLastStateChangeTransaction()!!
        val oldState = oldTx.action.type
        if(!challenge.canTransitionState(oldState, newState))
            return SOAResult(SOAResultType.FAILURE, "Cannot transition from ${oldState.type} to ${newState.type}")

        // Redeem challenge
        val redemptionResult = RedeemChallengeService.execute(caller, challenge, completerPublicKey)

        if(redemptionResult.result != SOAResultType.SUCCESS)
            return redemptionResult

        // transition state
        val stateChangeResult = ChangeChallengeStateService.execute(caller, challenge.idValue, ActionType.COMPLETE)
        if(stateChangeResult.result != SOAResultType.SUCCESS)
            return SOAResult(SOAResultType.FAILURE, stateChangeResult.message)

        return redemptionResult
    }
}