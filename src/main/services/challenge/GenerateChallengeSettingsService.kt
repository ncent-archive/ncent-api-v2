package main.services.challenge

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.*

/**
 * Generate a reward if it is valid
 */
object GenerateChallengeSettingsService {
    fun execute(caller: UserAccount, challengeSettingNamespace: ChallengeSettingNamespace) : SOAResult<ChallengeSetting> {
        return SOAResult(SOAResultType.SUCCESS, null, ChallengeSetting.new {
            name = challengeSettingNamespace.name
            description = challengeSettingNamespace.description
            imageUrl = challengeSettingNamespace.imageUrl
            sponsorName = challengeSettingNamespace.sponsorName
            expiration = challengeSettingNamespace.expiration
            shareExpiration = challengeSettingNamespace.shareExpiration
            admin = caller.id
            offChain = challengeSettingNamespace.offChain
            maxShares = challengeSettingNamespace.maxShares
            maxRewards = challengeSettingNamespace.maxRewards
            maxDistributionFeeReward = challengeSettingNamespace.maxDistributionFeeReward
            maxSharesPerReceivedShare = challengeSettingNamespace.maxSharesPerReceivedShare
            maxDepth = challengeSettingNamespace.maxDepth
            maxNodes = challengeSettingNamespace.maxNodes
        })
    }
}