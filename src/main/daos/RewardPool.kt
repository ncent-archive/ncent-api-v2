package main.daos

import framework.models.BaseIntEntity
import framework.models.BaseIntEntityClass
import framework.models.BaseIntIdTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.ReferenceOption

/**
 * RewardPool; model representing transactions related to a particular reward
 * The pool of all of the RewardPool (transactions) signifies the tokens available.
 *
 * @property id
 * @property cryptoKeyPair public address, allowing the reward pool to do token transfers
 * @property reward linking back to a particular reward
 * @property transactions particular transaction for this pool
 */
class RewardPool(id: EntityID<Int>) : BaseIntEntity(id, RewardPools) {
    companion object : BaseIntEntityClass<RewardPool>(RewardPools)

    var cryptoKeyPair by CryptoKeyPair referencedOn RewardPools.cryptoKeyPair
    var transactions by Transaction via RewardsToTransactions

    override fun toMap(): MutableMap<String, Any?> {
        var map = super.toMap()
        map.put("cryptoKeyPair", cryptoKeyPair.toMap())
        map.put("transactions", transactions.map { it.toMap() })
        return map
    }
}

object RewardPools : BaseIntIdTable("reward_pools") {
    val cryptoKeyPair = reference("crypto_key_pair", CryptoKeyPairs, onDelete = ReferenceOption.CASCADE)
}

object RewardsToTransactions : BaseIntIdTable("rewards_to_transactions") {
    val rewardPool = reference("reward_pool_to_transaction", RewardPools, onDelete = ReferenceOption.CASCADE).primaryKey()
    val transactions = reference("transaction_to_reward", Transactions, onDelete = ReferenceOption.CASCADE).primaryKey()
}