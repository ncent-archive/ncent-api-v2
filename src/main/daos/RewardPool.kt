package main.daos

import framework.models.BaseIntEntity
import framework.models.BaseIntEntityClass
import framework.models.BaseIntIdTable
import org.jetbrains.exposed.dao.EntityID

/**
 * RewardPool; model representing transactions related to a particular reward
 * The pool of all of the RewardPool (transactions) signifies the tokens available.
 *
 * @property id
 * @property reward linking back to a particular reward
 * @property transactions particular transaction for this pool
 */
class RewardPool(id: EntityID<Int>) : BaseIntEntity(id, RewardPools) {
    companion object : BaseIntEntityClass<RewardPool>(RewardPools)

    var reward by RewardPools.reward
    // TODO: change to user referrersOn
    var transactions by RewardPools.transactions
}

object RewardPools : BaseIntIdTable("reward_pools") {
    val reward = reference("reward", Rewards)
    val transactions = reference("transactions", Transactions)
}