package main.transactions.models

/**
 * Representation of an action taking place and being stored in a transaction
 * @property type This is the action type; ex: transfer, create, share, payout.
 * @property data The data object; ex: a particular token, a particular Challenge
 * @property dataType This is the object type; ex: Token, Challenge.
 */
data class Action<T>(
        var type: String,
        var data: T?,
        var dataType: String?
)