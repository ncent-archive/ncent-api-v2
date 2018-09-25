package main.transactions.models

import kotlinserverless.framework.models.BaseModel

/**
 * Representation of a Token -- used when transfering/sharing tokens via transactions
 *
 * @property id
 * @property amount
 * @property tokenType
 */
data class Token(
        override var id: Int?,
        var amount: Int,
        var tokenType: TokenType

) : BaseModel()