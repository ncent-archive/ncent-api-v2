package main.services.challenge

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import org.joda.time.DateTime

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
            expiration = DateTime.parse(challengeSettingNamespace.expiration)
            shareExpiration = DateTime.parse(challengeSettingNamespace.shareExpiration)
            admin = caller
            offChain = challengeSettingNamespace.offChain
            maxShares = challengeSettingNamespace.maxShares
            maxRewards = challengeSettingNamespace.maxRewards?.toInt()
            maxDistributionFeeReward = challengeSettingNamespace.maxDistributionFeeReward?.toInt()
            maxSharesPerReceivedShare = challengeSettingNamespace.maxSharesPerReceivedShare?.toInt()
            maxDepth = challengeSettingNamespace.maxDepth?.toInt()
            maxNodes = challengeSettingNamespace.maxNodes?.toInt()
        })
    }
}