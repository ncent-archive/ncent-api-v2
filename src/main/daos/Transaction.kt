package main.daos

import framework.models.BaseIntEntity
import framework.models.BaseIntEntityClass
import framework.models.BaseIntIdTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.Table

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
 * @property metadatas Optionally can be used to keep track of additional data. (ex: max shares)
 * example being: challenge sharing (providence chain)
 */
class Transaction(id: EntityID<Int>) : BaseIntEntity(id, Transactions) {
    companion object : BaseIntEntityClass<Transaction>(Transactions)

    var from by Transactions.from
    var to by Transactions.to
    var action by Action referencedOn Transactions.action
    var previousTransaction by Transaction optionalReferencedOn Transactions.previousTransaction
    var metadatas by Metadata via TransactionsMetadata
}

object Transactions : BaseIntIdTable("transactions") {
    val from = varchar("from", 256)
    val to = varchar("to", 256).nullable()
    val action = reference("action", Actions)
    val previousTransaction = optReference("previous_transaction", Transactions)
}

object TransactionsMetadata : Table("transactions_to_metadatas") {
    val transaction = reference("transaction_to_metadatas", Transactions).primaryKey()
    val metadata = reference("metadata_to_transaction", Metadatas).primaryKey()
}

data class TransactionNamespace(val from: String, val to: String?, val action: ActionNamespace?, val previousTransaction: Int?, val metadatas: MetadatasListNamespace?)

class TransactionList(val transactions: List<Transaction>)