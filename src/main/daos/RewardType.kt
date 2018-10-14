package main.daos

import framework.models.BaseIntEntity
import framework.models.BaseIntEntityClass
import framework.models.BaseIntIdTable
import org.jetbrains.exposed.dao.EntityID

/**
 * RewardType signifies the way the rewards will be distributed amongst the entire chain or the providence chain
 *
 * @property id
 * @property audience example: [PROVIDENCE, FULL]
 * @property type example: [SINGLE, EVEN, TIERED, LOGARITHMIC, EXPONENTIAL_UP, EXPONENTIAL_DOWN]
 */
class RewardType(id: EntityID<Int>) : BaseIntEntity(id, RewardTypes) {
    companion object : BaseIntEntityClass<RewardType>(RewardTypes)

    var audience by RewardTypes.audience
    // TODO: change to use referrersOn
    var type by RewardTypes.type
}

object RewardTypes : BaseIntIdTable("reward_types") {
    val audience = enumeration("audience", Audience::class)
    val type = enumeration("type", RewardTypeName::class)
}

enum class Audience {
    PROVIDENCE, FULL
}

enum class RewardTypeName {
    SINGLE, EVEN, TIERED, LOGARITHMIC, EXPONENTIAL_UP, EXPONENTIAL_DOWN
}