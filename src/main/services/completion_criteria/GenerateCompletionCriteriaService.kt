package main.services.completion_criteria

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.*
import main.services.reward.GenerateRewardService

/**
 * Generate a new completion criteria
 */
object GenerateCompletionCriteriaService: SOAServiceInterface<CompletionCriteria> {
    override fun execute(caller: Int?, d: Any?, params: Map<String, String>?) : SOAResult<CompletionCriteria> {
        val completionCriteriaNamespace = d!! as CompletionCriteriaNamespace
        val rewardResult = GenerateRewardService.execute(caller, completionCriteriaNamespace.rewardNamespace, params)
        if(rewardResult.result != SOAResultType.SUCCESS)
            return SOAResult(SOAResultType.FAILURE, rewardResult.message)

        val completionAddress = if(completionCriteriaNamespace.address != null) {
            completionCriteriaNamespace.address
        } else {
            UserAccount.findById(caller!!)!!.cryptoKeyPair.publicKey
        }

        // TODO generate a transaction

        if(completionCriteriaNamespace.preReqChallengeIds.any()) {
            val prereqChallenges = Challenge.find {
                Challenges.id inList completionCriteriaNamespace.preReqChallengeIds!!
            }
            return SOAResult(SOAResultType.SUCCESS, null, CompletionCriteria.new {
                address = completionAddress
                reward = rewardResult.data!!
                prereq = prereqChallenges
            })
        } else {
            return SOAResult(SOAResultType.SUCCESS, null, CompletionCriteria.new {
                address = completionAddress
                reward = rewardResult.data!!
            })
        }
    }
}