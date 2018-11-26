package main.daos

import framework.models.BaseIntEntity
import framework.models.BaseIntEntityClass
import framework.models.BaseIntIdTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.Table


/**
 * Reward
 *
 * @property id
 * @property type RewardType
 * @property pool List<RewardPool>
 */
class Reward(id: EntityID<Int>) : BaseIntEntity(id, Rewards) {
    companion object : BaseIntEntityClass<Reward>(Rewards)

    var type by Rewards.type
    // TODO: change to user referrersOn
    var pool by Rewards.pool
    var metadatas by Metadata via RewardsMetadata
}

object RewardsMetadata : Table("rewards_to_metadatas") {
    val reward = reference("reward_to_metadatas", Rewards).primaryKey()
    val metadata = reference("metadata_to_reward", Metadatas).primaryKey()
}

object Rewards : BaseIntIdTable("rewards") {
    val type = reference("type", RewardTypes)
    val pool = reference("pool", RewardPools).nullable()
}

data class RewardNamespace(val type: RewardTypeNamespace, val metadatas: MetadatasListNamespace?)