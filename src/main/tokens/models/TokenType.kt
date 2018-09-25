package main.tokens.models

import kotlinserverless.framework.models.BaseModel

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
data class TokenType(
        override var id: Int?,
        var name: String,
        var parentToken: TokenType?,
        var parentTokenConversionRate: Double?

) : BaseModel()