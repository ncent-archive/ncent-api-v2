package main.daos

import framework.models.BaseIntEntity
import framework.models.BaseIntEntityClass
import framework.models.BaseIntIdTable
import org.jetbrains.exposed.dao.EntityID

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

    var completionCriteria by CompletionCriteria referencedOn ResultVectors.completionCriteria
    var reward by Reward referencedOn ResultVectors.reward
    var distributionFeeReward by Reward referencedOn ResultVectors.distributionFeeReward
}

//TODO CHANGE rewards to be in completion critera -- and have one to many challenge to completion criterias

object ResultVectors : BaseIntIdTable("result_vectors") {
    var challenge = reference("challenge_to_result_vector", Challenges)
    var completionCriteria = reference("completion_criteria", CompletionCriterias)
    var reward = reference("reward", Rewards)
    var distributionFeeReward = reference("distribution_fee_reward", Rewards)
}