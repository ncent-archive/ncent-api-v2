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
 * @property type example: [SINGLE, EVEN, LOGARITHMIC, EXPONENTIAL]
 */
class RewardType(id: EntityID<Int>) : BaseIntEntity(id, RewardTypes) {
    companion object : BaseIntEntityClass<RewardType>(RewardTypes)

    var audience by RewardTypes.audience
    var type by RewardTypes.type

    override fun toMap(): MutableMap<String, Any?> {
        var map = super.toMap()
        map.put("audience", audience)
        map.put("type", type)
        return map
    }
}

object RewardTypes : BaseIntIdTable("reward_types") {
    val audience = enumeration("audience", Audience::class)
    val type = enumeration("type", RewardTypeName::class)
}

enum class Audience(val str: String) {
    PROVIDENCE("providence"), FULL("full")
}

enum class RewardTypeName(val str: String) {
    SINGLE("single"), EVEN("even"), LOGARITHMIC("log"), EXPONENTIAL("exp"), N_OVER_2("nover2")
}

data class RewardTypeNamespace(val audience: Audience, val type: RewardTypeName)