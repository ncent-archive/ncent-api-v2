package main.daos

import framework.models.BaseIntEntity
import framework.models.BaseIntEntityClass
import framework.models.BaseIntIdTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.dao.EntityID

/**
 * Used to represent end results for a challenge user interacting with the challenge
 * Users can either complete and be rewarded, or withdraw and be rewarded from the distribution fee
 *
 * @property completionCriteria used to validate completion, ex: oracle, admin, contract
 * @property distributionFeeReward the distribution fees and the pool. this is the
 * pool that will be drawn on if anybody 'opts-out' of attempting to help.
 */
class ResultVector(id: EntityID<Int>) : BaseIntEntity(id, ResultVectors) {
    companion object : BaseIntEntityClass<ResultVector>(ResultVectors)

    var completionCriteria by CompletionCriteria via ResultVectorsCompletionCriteria
    var distributionFeeReward by Reward referencedOn ResultVectors.distributionFeeReward
}

object ResultVectorsCompletionCriteria : Table("result_vectors_completion_criteria") {
    val resultVector = reference("result_vector_to_completion_criteria", ResultVectors).primaryKey()
    val completionCriteria = reference("completion_criteria_to_result_vector", CompletionCriterias).primaryKey()
}

object ResultVectors : BaseIntIdTable("result_vectors") {
    val challenge = reference("challenge_to_result_vector", Challenges)
    val distributionFeeReward = reference("distribution_fee_reward", Rewards)
}