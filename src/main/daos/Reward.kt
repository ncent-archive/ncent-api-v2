package main.daos

import framework.models.BaseIntEntity
import framework.models.BaseIntEntityClass
import framework.models.BaseIntIdTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.ReferenceOption
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

    var type by RewardType referencedOn Rewards.type
    var pool by RewardPool referencedOn Rewards.pool
    var metadatas by Metadata via RewardsMetadata

    override fun toMap(): MutableMap<String, Any?> {
        var map = super.toMap()
        map.put("type", type.toMap())
        map.put("pool", pool.toMap())
        map.put("metadatas", metadatas.map { it.toMap() })
        return map
    }
}

object RewardsMetadata : BaseIntIdTable("rewards_to_metadatas") {
    val reward = reference("reward_to_metadatas", Rewards, onDelete = ReferenceOption.CASCADE)
    val metadata = reference("metadata_to_reward", Metadatas, onDelete = ReferenceOption.CASCADE)
}

object Rewards : BaseIntIdTable("rewards") {
    val type = reference("type", RewardTypes, onDelete = ReferenceOption.CASCADE)
    val pool = reference("pool", RewardPools, onDelete = ReferenceOption.CASCADE)
}

data class RewardNamespace(val type: RewardTypeNamespace, val metadatas: Array<MetadatasNamespace>? = null)