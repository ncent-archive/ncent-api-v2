package main.controllers

import kotlinserverless.framework.controllers.RestController
import kotlinserverless.framework.controllers.DefaultController
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.models.Request
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import main.services.user_account.ValidateApiKeyService
import main.services.challenge.*
import main.helpers.JsonHelper
import main.services.completion_criteria.GenerateCompletionCriteriaService
import main.services.reward.GenerateRewardService

class ChallengeController: DefaultController<Challenge>(), RestController<Challenge, UserAccount> {
    override fun create(user: UserAccount, params: Map<String, String>): SOAResult<Challenge> {
        val result = SOAResult<Challenge>(SOAResultType.FAILURE, null, null)

        val apiCred = user.apiCreds
        val validateApiKeyResult = ValidateApiKeyService.execute(apiCred.apiKey, apiCred.secretKey)
        if (validateApiKeyResult.result != SOAResultType.SUCCESS) {
            result.message = validateApiKeyResult.message
            return result
        }
        
        val parentChallenge: Int? = params["parentChallenge"]?.toInt()

        val challengeSettingNamespace = JsonHelper.parse<ChallengeSettingNamespace>(params["challengeSettings"]!!)
        val generateChallengeSettingsResult = GenerateChallengeSettingsService.execute(user, challengeSettingNamespace)
        if (generateChallengeSettingsResult.result != SOAResultType.SUCCESS) {
            result.message = generateChallengeSettingsResult.message
            return result
        }
        
        val completionCriteriaNamespace = JsonHelper.parse<CompletionCriteriaNamespace>(params["completionCriteria"]!!)
        val generateCompletionCriteriaResult = GenerateCompletionCriteriaService.execute(user, completionCriteriaNamespace)
        if (generateCompletionCriteriaResult.result != SOAResultType.SUCCESS) {
            result.message = generateCompletionCriteriaResult.message
            return result
        }

        val rewardNamespace = JsonHelper.parse<RewardNamespace>(params["rewardNamespace"]!!)
        val generateRewardResult = GenerateRewardService.execute(rewardNamespace)
        if (generateRewardResult.result != SOAResultType.SUCCESS) {
            result.message = generateRewardResult.message
            return result
        }

        val challengeNamespace = ChallengeNamespace(
                parentChallenge,
                challengeSettingNamespace,
                null,
                completionCriteriaNamespace,
                rewardNamespace
        )
        val generateChallengeResult = GenerateChallengeService.execute(user, challengeNamespace)
        if (generateChallengeResult.result == SOAResultType.SUCCESS) {
            AddSubChallengeService.execute(user, challengeNamespace, parentChallenge!!, SubChallengeType.valueOf(params["subChallengeType"] as String))
        }

        return generateChallengeResult
    }

    override fun findOne(user: UserAccount, id: Int): SOAResult<Challenge> {
        throw NotImplementedError()
    }

    fun expire(user: UserAccount, request: Request): SOAResult<Challenge> {
        throw NotImplementedError()
    }

    fun invalidate(user: UserAccount, request: Request): SOAResult<Challenge> {
        throw NotImplementedError()
    }

    fun validate(user: UserAccount, request: Request): SOAResult<Boolean> {
        throw NotImplementedError()
    }

    fun complete(user: UserAccount, request: Request): SOAResult<Challenge> {
        throw NotImplementedError()
    }

    fun share(user: UserAccount, request: Request): SOAResult<Challenge> {
        throw NotImplementedError()
    }
}