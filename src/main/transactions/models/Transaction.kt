package main.transactions.models

import kotlinserverless.framework.models.BaseModel

/**
 * Transaction represents the data that records any changes related to any
 * object. This will be stored in an immutable way to represent how it would
 * be stored on a blockchain. This can also track off-chain transactions; which this
 * model can easily represent, however will not be stored.
 *
 * @property id
 * @property from Address that triggers this transaction
 * @property to Optionally an address this transaction is sending to
 * @property action The action taking place
 * @property previousTransaction Optionally the previous transaction related to this transaction
 * example being: challenge sharing (providence chain)
 */
data class Transaction<T>(
        override var id: Int?,
        var from: String,
        var to: String?,
        var action: Action<T>,
        var previousTransaction: Transaction<T>?

) : BaseModel()