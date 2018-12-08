package main.services.challenge

import framework.services.DaoService
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.*
import org.jetbrains.exposed.dao.EntityID

/**
 * Generate a reward if it is valid
 */
object GenerateChallengeSettingsService: SOAServiceInterface<ChallengeSetting> {
    override fun execute(caller: Int?, d: Any?, params: Map<String, String>?) : SOAResult<ChallengeSetting> {
        val challengeSettingNamespace = d!! as ChallengeSettingNamespace
        return DaoService.execute {
            return@execute ChallengeSetting.new {
                name = challengeSettingNamespace.name
                description = challengeSettingNamespace.description
                description = challengeSettingNamespace.description
                imageUrl = challengeSettingNamespace.imageUrl
                sponsorName = challengeSettingNamespace.sponsorName
                expiration = challengeSettingNamespace.expiration
                admin = EntityID(challengeSettingNamespace.admin, ChallengeSettings)
                offChain = challengeSettingNamespace.offChain
                maxRewards = challengeSettingNamespace.maxRewards
                maxDistributionFeeReward = challengeSettingNamespace.maxDistributionFeeReward
                maxSharesPerReceivedShare = challengeSettingNamespace.maxSharesPerReceivedShare
                maxDepth = challengeSettingNamespace.maxDepth
                maxNodes = challengeSettingNamespace.maxNodes
            }
        }
    }
}