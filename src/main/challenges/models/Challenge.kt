package main.challenges.models

import kotlinserverless.framework.models.BaseModel
import main.completionCriterias.models.CompletionCriteria
import main.rewards.models.Reward
import org.joda.time.DateTime

/**
 * Used to represent challenges and store pointers to models that
 * store the state for a challenge over the lifecycle of that challenge
 *
 * @property id
 * @property name
 * @property expiration
 * @property admin address of owner of this challenge
 * @property offChain optionally set this challenge to allow off-chain sharing
 * @property maxRewards the number of times the reward can be claimed
 * @property maxDistributionFeeReward the number of times the distribution fee
 * can be rewarded
 * @property synchronousChallenges sub challenges that must be completed in order
 * @property asynchronousChallenges sub challenges that can be completed in any order
 * @property completionCriteria used to validate completion, ex: oracle, admin, contract
 * @property reward the reward amount and it's pool
 * @property distributionFeeReward the distribution fees and the pool. this is the
 * pool that will be drawn on if anybody 'opts-out' of attempting to help.
 */
data class Challenge(
        override var id: Int?,
        val challengeSettings: ChallengeSettings,
        val subChallenges: SubChallenges,
        val resultVectors: ResultVectors
) : BaseModel()


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
 */
data class ChallengeSettings(
        val name: String,
        val expiration: DateTime,
        val admin: String,
        val offChain: Boolean = false,
        val maxRewards: Int = 1,
        val maxDistributionFeeReward: Int = 1
)

/**
 * Used to represent optional sub challenges
 *
 * @property synchronousChallenges sub challenges that must be completed in order
 * @property asynchronousChallenges sub challenges that can be completed in any order
 */
data class SubChallenges(
        val synchronousChallenges: List<Challenge>?,
        val asynchronousChallenges: Map<Int, Challenge>?
)

/**
 * Used to represent end results for a challenge user interacting with the challenge
 * Users can either complete and be rewarded, or withdraw and be rewarded from the distribution fee
 *
 * @property completionCriteria used to validate completion, ex: oracle, admin, contract
 * @property reward the reward amount and it's pool
 * @property distributionFeeReward the distribution fees and the pool. this is the
 * pool that will be drawn on if anybody 'opts-out' of attempting to help.
 */
data class ResultVectors(
        val completionCriteria: CompletionCriteria,
        val reward: Reward,
        val distributionFeeReward: Reward
)