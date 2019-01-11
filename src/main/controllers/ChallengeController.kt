package main.controllers

import kotlinserverless.framework.controllers.RestController
import kotlinserverless.framework.controllers.DefaultController
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.models.Request
import main.daos.*
import main.services.user_account.ValidateApiKeyService
import main.services.challenge.*
import main.helpers.JsonHelper
import org.joda.time.DateTime

class ChallengeController: DefaultController<Challenge>(), RestController<Challenge, UserAccount> {
    override fun create(user: UserAccount, params: Map<String, String>): SOAResult<Challenge> {
        val apiCred = user.apiCreds

        ValidateApiKeyService.execute(apiCred.apiKey, apiCred.secretKey)

        val parentChallenge: Int? = params["parentChallenge"]?.toInt()

        val challengeSettings = JsonHelper.parse(params["challengeSettings"]!!)
        val challengeSettingNamespace = ChallengeSettingNamespace(
                challengeSettings["name"] as String,
                challengeSettings["description"] as String,
                challengeSettings["imageUrl"] as String,
                challengeSettings["sponsorName"] as String,
                challengeSettings["expiration"] as DateTime,
                challengeSettings["shareExpiration"] as DateTime,
                challengeSettings["admin"] as Int,
                challengeSettings["maxShares"] as Int,
                challengeSettings["offChain"] as Boolean,
                challengeSettings["maxRewards"] as Int?,
                challengeSettings["maxDistributionFeeReward"] as Int?,
                challengeSettings["maxSharesPerReceivedShare"] as Int?,
                challengeSettings["maxDepth"] as Int?,
                challengeSettings["maxNodes"] as Int?
        )
        GenerateChallengeSettingsService.execute(user, challengeSettingNamespace)

        val completionCriteria = JsonHelper.parse(params["completionCriteria"]!!)
        //Todo: parse and form prerequisites list for completion criteria namespace
        val completionCriteriaNamespace = CompletionCriteriaNamespace(

        )


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