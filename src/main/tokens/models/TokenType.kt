package main.tokens.models

import framework.models.BaseIntEntity
import framework.models.BaseIntEntityClass
import framework.models.BaseIntIdTable
import org.jetbrains.exposed.dao.EntityID

/**
 * Representation of a TokenType
 *
 * @property id
 * @property name
 * @property parentToken -- if it is null is it a base nCentToken
 * @property parentTokenConversionRate this represents how many of this token would be
 * required to covert to a single parent token. EX: 100 coke tokens -> 1 ncentToken would
 * mean that the parentTokenConversionRate is 100.
 */
class TokenType(id: EntityID<Int>) : BaseIntEntity(id, TokenTypes) {
    companion object : BaseIntEntityClass<TokenType>(TokenTypes)

    var name by TokenTypes.name
    var parentToken by TokenTypes.parentToken
    var parentTokenConversionRate by TokenTypes.parentTokenConversionRate
}

object TokenTypes : BaseIntIdTable("token_types") {
    val name = varchar("name", 100)
    val parentToken = reference("parent_token", TokenTypes)
    val parentTokenConversionRate = double("parent_token_conversion_rate")
}