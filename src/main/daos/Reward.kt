package main.daos

import framework.models.BaseIntEntity
import framework.models.BaseIntEntityClass
import framework.models.BaseIntIdTable
import org.jetbrains.exposed.dao.EntityID


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
    // TODO: change to use referrersOn
    var pool by Rewards.pool
}

object Rewards : BaseIntIdTable("rewards") {
    val type = reference("type", RewardTypes)
    val pool = reference("pool", RewardPools)
}