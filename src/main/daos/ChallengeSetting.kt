package main.daos

import framework.models.BaseIntEntity
import framework.models.BaseIntEntityClass
import framework.models.BaseIntIdTable
import org.jetbrains.exposed.dao.EntityID
import org.joda.time.DateTime

/**
 * Used to represent challenges settings that are initially setup for every challenge
 *
 * @property name
 * @property expiration
 * @property shareExpiration
 * @property description
 * @property imageUrl
 * @property sponsorName
 * @property admin address of owner of this challenge
 * @property offChain optionally set this challenge to allow off-chain sharing
 * @property maxRewards the number of times the reward can be claimed
 * @property maxDistributionFeeReward the number of times the distribution fee
 * can be rewarded
 * @property maxSharesPerReceivedShare maximum times someone can share a challenge
 * per unique share they recieve
 * @property maxDepth the maximum depth of the providence chain
 * @property maxNodes the maximum number of nodes in the entire share graph
 */
class ChallengeSetting(id: EntityID<Int>) : BaseIntEntity(id, ChallengeSettings) {
    companion object : BaseIntEntityClass<ChallengeSetting>(ChallengeSettings)

    var name by ChallengeSettings.name
    var description by ChallengeSettings.description
    var imageUrl by ChallengeSettings.imageUrl
    var sponsorName by ChallengeSettings.sponsorName
    var expiration by ChallengeSettings.expiration
    var shareExpiration by ChallengeSettings.shareExpiration
    var admin by ChallengeSettings.admin
    var offChain by ChallengeSettings.offChain
    var maxShares by ChallengeSettings.maxShares
    var maxRewards by ChallengeSettings.maxRewards
    var maxDistributionFeeReward by ChallengeSettings.maxDistributionFeeReward
    var maxSharesPerReceivedShare by ChallengeSettings.maxSharesPerReceivedShare
    var maxDepth by ChallengeSettings.maxDepth
    var maxNodes by ChallengeSettings.maxNodes

    override fun toMap(): MutableMap<String, Any?> {
        var map = super.toMap()
        map.put("name", name)
        map.put("description", description)
        map.put("imageUrl", imageUrl)
        map.put("sponsorName", sponsorName)
        map.put("expiration", expiration.toString())
        map.put("shareExpiration", shareExpiration.toString())
        map.put("admin", admin)
        map.put("offChain", offChain)
        map.put("maxShares", maxShares)
        map.put("maxRewards", maxRewards)
        map.put("maxDistributionFeeReward", maxDistributionFeeReward)
        map.put("maxSharesPerReceivedShare", maxSharesPerReceivedShare)
        map.put("maxDepth", maxDepth)
        map.put("maxNodes", maxNodes)
        return map
    }
}

object ChallengeSettings : BaseIntIdTable("challenge_settings") {
    var name = varchar("name", 100)
    var description = varchar("description", 1000)
    var imageUrl = varchar("imageUrl", 100)
    var sponsorName = varchar("sponsorName", 100)
    var expiration = datetime("expiration")
    var shareExpiration = datetime("shareExpiration")
    var admin = reference("admin", UserAccounts)
    var offChain = bool("off_chain").default(false)
    var maxShares = integer("max_shares")
    var maxRewards = integer("max_rewards").default(1).nullable()
    var maxDistributionFeeReward = integer("max_distribution_fee_reward").default(Integer.MAX_VALUE).nullable()
    var maxSharesPerReceivedShare = integer("max_shares_per_received_share").default(Integer.MAX_VALUE).nullable()
    var maxDepth = integer("max_depth").default(Integer.MAX_VALUE).nullable()
    var maxNodes = integer("max_nodes").default(Integer.MAX_VALUE).nullable()
}

data class ChallengeSettingNamespace(
    val name: String,
    val description: String,
    val imageUrl: String,
    val sponsorName: String,
    val expiration: DateTime,
    val shareExpiration: DateTime,
    val admin: Int,
    val maxShares: Int,
    val offChain: Boolean,
    val maxRewards: Int?,
    val maxDistributionFeeReward: Int?,
    val maxSharesPerReceivedShare: Int?,
    val maxDepth: Int?,
    val maxNodes: Int?
)