package main.transactions.models

/**
 *
 */
data class Action<T>(
        var type: String,
        var data: T?,
        var dataType: String?,
        var amount: Int?
)