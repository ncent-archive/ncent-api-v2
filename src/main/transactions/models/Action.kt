package main.transactions.models

import framework.models.BaseIntEntity
import framework.models.BaseIntEntityClass
import framework.models.BaseIntIdTable
import main.tokens.models.TokenType
import org.jetbrains.exposed.dao.EntityID

/**
 * Representation of an action taking place and being stored in a transaction
 * @property type This is the action type; ex: transfer, create, share, payout.
 * @property data The data object; ex: a particular token, a particular Challenge
 * @property dataType This is the object type; ex: Token, Challenge.
 */
class Action(id: EntityID<Int>) : BaseIntEntity(id, Actions) {
    companion object : BaseIntEntityClass<TokenType>(Actions)

    var type by Actions.type
    var data by Actions.data
    var dataType by Actions.dataType
}

object Actions : BaseIntIdTable("actions") {
    val type = enumeration("type", ActionType::class)
    val data = integer("data_id")
    val dataType = varchar("class_name", 100)
}

enum class ActionType {
    TRANSFER, CREATE, SHARE, PAYOUT
}