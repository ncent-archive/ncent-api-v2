package main.daos

import framework.models.BaseIntEntity
import framework.models.BaseIntEntityClass
import framework.models.BaseIntIdTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.ReferenceOption

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
    var parentToken by TokenType optionalReferencedOn TokenTypes.parentToken
    var parentTokenConversionRate by TokenTypes.parentTokenConversionRate

    override fun toMap(): MutableMap<String, Any?> {
        var map = super.toMap()
        map.put("name", name)
        map.put("parentToken", parentToken?.toMap())
        map.put("parentTokenConversionRate", parentTokenConversionRate)
        return map
    }
}

object TokenTypes : BaseIntIdTable("token_types") {
    val name = varchar("name", 100).uniqueIndex()
    val parentToken = reference("parent_token", TokenTypes, onDelete = ReferenceOption.CASCADE).nullable()
    val parentTokenConversionRate = double("parent_token_conversion_rate").nullable()
}

data class TokenTypeNamespace(val id: Int?, val name: String, val parentToken: Int?, val parentTokenConversionRate: Double?)