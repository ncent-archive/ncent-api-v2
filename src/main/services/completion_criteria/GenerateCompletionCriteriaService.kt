package main.services.completion_criteria

import framework.services.DaoService
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.*
import main.services.reward.GenerateRewardService
import main.services.user_account.GenerateCryptoKeyPairService
import java.lang.Exception

/**
 * Generate a new completion criteria
 */
object GenerateCompletionCriteriaService: SOAServiceInterface<CompletionCriteria> {
    override fun execute(caller: Int?, d: Any?, params: Map<String, String>?) : SOAResult<CompletionCriteria> {
        val completionCriteriaNamespace = d!! as CompletionCriteriaNamespace
        return DaoService.execute {
            val rewardResult = GenerateRewardService.execute(caller, completionCriteriaNamespace.rewardNamespace, params)
            if(rewardResult.result != SOAResultType.SUCCESS)
                throw Exception(rewardResult.message)

            val completionAddress = if(completionCriteriaNamespace.address != null) {
                completionCriteriaNamespace.address
            } else {
                UserAccount.findById(caller!!)!!.cryptoKeyPair.publicKey
            }

            if(completionCriteriaNamespace.preReqCompletionCriteriaIds != null) {
                val prereqCompletionCriterias = CompletionCriteria.find {
                    CompletionCriterias.id inList completionCriteriaNamespace.preReqCompletionCriteriaIds!!
                }
                return@execute CompletionCriteria.new {
                    address = completionAddress
                    reward = rewardResult.data!!
                    expiration = completionCriteriaNamespace.expiration
                    prereq = prereqCompletionCriterias
                }
            } else {
                return@execute CompletionCriteria.new {
                    address = completionAddress
                    reward = rewardResult.data!!
                    expiration = completionCriteriaNamespace.expiration
                }
            }
        }
    }
}