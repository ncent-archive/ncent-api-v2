package main.daos

import framework.models.BaseIntEntity
import framework.models.BaseIntEntityClass
import framework.models.BaseIntIdTable
import org.jetbrains.exposed.dao.EntityID

/**
 * Representation of an action taking place and being stored in a transaction
 * @property type This is the action type; ex: transfer, create, share, payout.
 * @property data The data object; ex: a particular token, a particular Challenge
 * @property dataType This is the object type; ex: Token, Challenge.
 */
class Action(id: EntityID<Int>) : BaseIntEntity(id, Actions) {
    companion object : BaseIntEntityClass<Action>(Actions)

    var type by Actions.type
    var data by Actions.data
    var dataType by Actions.dataType

    override fun toMap(): MutableMap<String, Any?> {
        var map = super.toMap()
        map.put("type", type)
        map.put("data", data)
        map.put("dataType", dataType)
        return map
    }
}

object Actions : BaseIntIdTable("actions") {
    val type = enumeration("type", ActionType::class)
    val data = integer("data_id")
    val dataType = varchar("class_name", 100)
}

data class ActionNamespace(val type: ActionType?=null, val data: Int?=null, val dataType: String)

enum class ActionType(val type: String) {
    TRANSFER("transfer"),
    CREATE("create"),
    SHARE("share"),
    PAYOUT("payout"),
    ACTIVATE("activate"),
    COMPLETE("complete"),
    INVALIDATE("invalidate"),
    EXPIRE("expire"),
    UPDATE("update")
}