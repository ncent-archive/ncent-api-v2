package main.challenges.models

import framework.models.BaseIntEntity
import framework.models.BaseIntEntityClass
import framework.models.BaseIntIdTable
import kotlinserverless.main.users.models.Users
import main.completionCriterias.models.CompletionCriterias
import main.rewards.models.Rewards
import org.jetbrains.exposed.dao.*

/**
 * Used to represent challenges and store pointers to models that
 * store the state for a challenge over the lifecycle of that challenge
 *
 * @property id
 * @property challengeSettings ChallengeSettings
 * @property asyncSubChallenges sub challenges that can be completed in any order
 * @property syncSubChallenges sub challenges that must be completed in order
 * @property resultVectors ResultVectors
 */
class Challenge(id: EntityID<Int>) : BaseIntEntity(id, Challenges) {
    companion object : BaseIntEntityClass<Challenge>(Challenges)

    var challengeSettings by Challenges.challengeSettings
    // TODO: change to use referrersOn
    var asyncSubChallenges by Challenges.asyncSubChallenges
    // TODO: change to use referrersOn
    var syncSubChallenges by Challenges.syncSubChallenges
    var resultVectors by Challenges.resultVectors
}

object Challenges : BaseIntIdTable("challenges") {
    val challengeSettings = reference("challenge_settings", ChallengeSettings)
    val asyncSubChallenges = reference("challenges", Challenges)
    val syncSubChallenges = reference("challenges", Challenges)
    val resultVectors = reference("result_vectors", ResultVectors)
}


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

/**
 * Used to represent end results for a challenge user interacting with the challenge
 * Users can either complete and be rewarded, or withdraw and be rewarded from the distribution fee
 *
 * @property completionCriteria used to validate completion, ex: oracle, admin, contract
 * @property reward the reward amount and it's pool
 * @property distributionFeeReward the distribution fees and the pool. this is the
 * pool that will be drawn on if anybody 'opts-out' of attempting to help.
 */
class ResultVector(id: EntityID<Int>) : BaseIntEntity(id, ResultVectors) {
    companion object : BaseIntEntityClass<ResultVector>(ResultVectors)

    var completionCriteria by ResultVectors.completionCriteria
    var reward by ResultVectors.reward
    var distributionFeeReward by ResultVectors.distributionFeeReward
}

object ResultVectors : BaseIntIdTable("result_vectors") {
    var completionCriteria = reference("completion_criteria", CompletionCriterias)
    var reward = reference("reward", Rewards)
    var distributionFeeReward = reference("distribution_fee_reward", Rewards)
}