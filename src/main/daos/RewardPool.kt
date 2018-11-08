package main.daos

import framework.models.BaseIntEntity
import framework.models.BaseIntEntityClass
import framework.models.BaseIntIdTable
import main.daos.RewardsToTransactions.primaryKey
import main.daos.TransactionsMetadata.primaryKey
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

    var reward by Reward referencedOn RewardPools.reward
    var transactions by Transaction via RewardsToTransactions
}

object RewardPools : BaseIntIdTable("reward_pools") {
    val reward = RewardsToTransactions.reference("reward", Rewards)
}

object RewardsToTransactions : BaseIntIdTable("rewards_to_transactions") {
    val reward = reference("reward_to_transaction", Rewards).primaryKey(0)
    val transactions = reference("transaction_to_reward", Transactions).primaryKey(1)
}