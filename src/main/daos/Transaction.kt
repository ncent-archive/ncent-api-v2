package main.daos

import framework.models.BaseIntEntity
import framework.models.BaseIntEntityClass
import framework.models.BaseIntIdTable
import org.jetbrains.exposed.dao.EntityID

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
class Transaction(id: EntityID<Int>) : BaseIntEntity(id, Transactions) {
    companion object : BaseIntEntityClass<Transaction>(Transactions)

    var from by Transactions.from
    var to by Transactions.to
    var action by Transactions.action
    var previousTransaction by Transactions.previousTransaction
}

object Transactions : BaseIntIdTable("transactions") {
    val from = varchar("from", 256)
    val to = varchar("to", 256)
    val action = reference("action", Actions)
    val previousTransaction = reference("previous_transaction", Transactions)
}