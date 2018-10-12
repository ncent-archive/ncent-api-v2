package main.tokens.models

import framework.models.BaseIntEntity
import framework.models.BaseIntEntityClass
import framework.models.BaseIntIdTable
import org.jetbrains.exposed.dao.EntityID

/**
 * Representation of a Token -- used when transfering/sharing tokens via transactions
 *
 * @property id
 * @property amount
 * @property tokenType
 */
class Token(id: EntityID<Int>) : BaseIntEntity(id, Tokens) {
    companion object : BaseIntEntityClass<Token>(Tokens)

    var amount by Tokens.amount
    var tokenType by Tokens.tokenType
}

object Tokens : BaseIntIdTable("tokens") {
    val amount = integer("amount")
    val tokenType = reference("token_type", TokenTypes)
}