package main.daos

import framework.models.BaseIntEntity
import framework.models.BaseIntEntityClass
import framework.models.BaseIntIdTable
import kotlinserverless.main.users.models.Users
import org.jetbrains.exposed.dao.EntityID

/**
 * Used to represent challenges settings that are initially setup for every challenge
 *
 * @property name
 * @property expiration
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
    var expiration by ChallengeSettings.expiration
    var admin by ChallengeSettings.admin
    var offChain by ChallengeSettings.offChain
    var maxRewards by ChallengeSettings.maxRewards
    var maxDistributionFeeReward by ChallengeSettings.maxDistributionFeeReward
    var maxSharesPerReceivedShare by ChallengeSettings.maxSharesPerReceivedShare
    var maxDepth by ChallengeSettings.maxDepth
    var maxNodes by ChallengeSettings.maxNodes
}

object ChallengeSettings : BaseIntIdTable("challenge_settings") {
    var name = varchar("name", 100)
    var expiration = datetime("expiration")
    var admin = reference("admin", Users)
    var offChain = bool("off_chain").default(false)
    var maxRewards = integer("max_rewards").default(1)
    var maxDistributionFeeReward = integer("max_distribution_fee_reward").default(Integer.MAX_VALUE)
    var maxSharesPerReceivedShare = integer("max_shares_per_received_share").default(Integer.MAX_VALUE)
    var maxDepth = integer("max_depth").default(Integer.MAX_VALUE)
    var maxNodes = integer("max_nodes").default(Integer.MAX_VALUE)
}