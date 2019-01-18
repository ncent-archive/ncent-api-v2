package main.services.completion_criteria

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import main.services.reward.GenerateRewardService

/**
 * Generate a new completion criteria
 */
object GenerateCompletionCriteriaService {
    fun execute(caller: UserAccount, completionCriteriaNamespace: CompletionCriteriaNamespace) : SOAResult<CompletionCriteria> {
        val rewardResult = GenerateRewardService.execute(completionCriteriaNamespace.reward)
        if(rewardResult.result != SOAResultType.SUCCESS)
            return SOAResult(SOAResultType.FAILURE, rewardResult.message)

        val completionAddress = if(completionCriteriaNamespace.address != null) {
            completionCriteriaNamespace.address
        } else {
            caller.cryptoKeyPair.publicKey
        }

        // TODO generate a transaction

        return if(completionCriteriaNamespace.prereq.any()) {
            val prereqChallenges = Challenge.find {
                Challenges.id inList completionCriteriaNamespace.prereq!!
            }
            SOAResult(SOAResultType.SUCCESS, null, CompletionCriteria.new {
                address = completionAddress
                reward = rewardResult.data!!
                prereq = prereqChallenges
            })
        } else {
            SOAResult(SOAResultType.SUCCESS, null, CompletionCriteria.new {
                address = completionAddress
                reward = rewardResult.data!!
            })
        }
    }
}