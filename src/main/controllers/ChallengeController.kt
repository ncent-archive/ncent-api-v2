package main.controllers

import framework.services.DaoService
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
    override fun create(user: UserAccount, params: Map<String, String>): SOAResult<*> {
        return DaoService.execute {
            val apiCred = user.apiCreds
            val validateApiKeyResult = ValidateApiKeyService.execute(apiCred.apiKey, params["secretKey"] as String)
            if (validateApiKeyResult.result != SOAResultType.SUCCESS) {
                DaoService.throwOrReturn(validateApiKeyResult.result, validateApiKeyResult.message)
            }

            val challengeNamespace = JsonHelper.parse<ChallengeNamespace>(params["challengeNamespace"]!!)

            val parentChallenge = challengeNamespace.parentChallenge

            val challengeSettingNamespace = challengeNamespace.challengeSettings
            val generateChallengeSettingsResult = GenerateChallengeSettingsService.execute(user, challengeSettingNamespace)
            if (generateChallengeSettingsResult.result != SOAResultType.SUCCESS) {
                DaoService.throwOrReturn(generateChallengeSettingsResult.result, generateChallengeSettingsResult.message)
            }

            val completionCriteriaNamespace = challengeNamespace.completionCriteria
            val generateCompletionCriteriaResult = GenerateCompletionCriteriaService.execute(user, completionCriteriaNamespace)
            if (generateCompletionCriteriaResult.result != SOAResultType.SUCCESS) {
                DaoService.throwOrReturn(generateCompletionCriteriaResult.result, generateCompletionCriteriaResult.message)
            }

            val rewardNamespace = challengeNamespace.distributionFeeReward
            val generateRewardResult = GenerateRewardService.execute(rewardNamespace)
            if (generateRewardResult.result != SOAResultType.SUCCESS) {
                DaoService.throwOrReturn(generateRewardResult.result, generateRewardResult.message)
            }

            val generateChallengeResult = GenerateChallengeService.execute(user, challengeNamespace)
            DaoService.throwOrReturn(generateChallengeResult.result, generateChallengeResult.message)
            if (generateChallengeResult.result == SOAResultType.SUCCESS) {
                AddSubChallengeService.execute(user, challengeNamespace, parentChallenge!!, SubChallengeType.valueOf(params["subChallengeType"] as String))
            }

            return@execute generateChallengeResult.data!!
        }
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