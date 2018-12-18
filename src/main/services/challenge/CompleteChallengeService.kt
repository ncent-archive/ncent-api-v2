package main.services.challenge

import framework.models.idValue
import framework.services.DaoService
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.*
import main.services.completion_criteria.ValidateCompletionCriteriaService
import main.services.reward.DistributeRewardService

/**
 * Trigger a challenge state change to active.
 */
object CompleteChallengeService: SOAServiceInterface<TransactionList> {
    override fun execute(caller: Int?, params: Map<String, String>?) : SOAResult<TransactionList> {
        var newParams = mutableMapOf<String,String>()
        val result = DaoService.execute {
            val challenge = Challenge.findById(params!!["challengeId"]!!.toInt())!!

            // check if the completion criteria matches.
            val validationResult = ValidateCompletionCriteriaService.execute(
                caller,
                mapOf(
                    Pair("completion_criteria_id", challenge.completionCriterias.idValue.toString())
                ))
            if(validationResult.result != SOAResultType.SUCCESS)
                throw Exception(validationResult.message)

            // validate we can do the state transition
            val newState = ActionType.valueOf("COMPLETE")
            val oldTx = challenge.getLastStateChangeTransaction()!!
            val oldState = oldTx.action.type
            if(!challenge.canTransitionState(oldState, newState))
                throw Exception("Cannot transition from ${oldState.type} to ${newState.type}")
            newParams["state"] = "COMPLETE"

            // transition state
            val stateChangeResult = ChangeChallengeStateService.execute(caller, newParams)
            if(stateChangeResult.result != SOAResultType.SUCCESS)
                throw Exception(stateChangeResult.message)

            // payout winner
            val rewardResult = DistributeRewardService.execute(
                caller,
                mapOf(
                    Pair("reward_id", challenge.completionCriterias.reward.idValue.toString()),
                    Pair("transaction_id", params["transactionId"]!!)
                ))
            if(rewardResult.result != SOAResultType.SUCCESS)
                throw Exception(rewardResult.message)
            return@execute rewardResult
        }
        if(result.result != SOAResultType.SUCCESS)
            return SOAResult(result.result, result.message, null)
        if(result.data!!.result != SOAResultType.SUCCESS)
            return SOAResult(result.data!!.result, result.data!!.message, null)
        return SOAResult(SOAResultType.SUCCESS, null, result.data!!.data)

    }
}