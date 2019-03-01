package main.daos

import framework.models.*
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.ReferenceOption
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

    override fun toMap(): MutableMap<String, Any?> {
        var map = super.toMap()
        map.put("from", from)
        map.put("to", to)
        map.put("action", action.toMap())
        map.put("previousTransactionId", previousTransaction?.idValue ?: "")
        map.put("metadatas", metadatas.map { it.toMap() })
        return map
    }
}

object Transactions : BaseIntIdTable("transactions") {
    val from = varchar("from", 256)
    val to = varchar("to", 256).nullable()
    val action = reference("action", Actions, onDelete = ReferenceOption.CASCADE)
    val previousTransaction = optReference("previous_transaction", Transactions, onDelete = ReferenceOption.CASCADE)
}

object TransactionsMetadata : BaseIntIdTable("transactions_to_metadatas") {
    val transaction = reference("transaction_to_metadatas", Transactions, onDelete = ReferenceOption.CASCADE)
    val metadata = reference("metadata_to_transaction", Metadatas, onDelete = ReferenceOption.CASCADE)
}

data class TransactionNamespace(val from: String?=null, val to: String?=null, val action: ActionNamespace?=null, val previousTransaction: Int?=null, val metadatas: Array<MetadatasNamespace>? = null)

class TransactionList(val transactions: List<Transaction>): BaseObject {
    override fun toMap(): MutableMap<String, Any?> {
        var map = mutableMapOf<String, Any?>()
        map.put("transactions", transactions.map { it.toMap() })
        return map
    }
}

data class TransactionNamespaceList(val transactions: List<TransactionNamespace>)

class TransactionToShare(val transaction: Transaction, val shares: Int): BaseObject {
    override fun toMap(): MutableMap<String, Any?> {
        var map = mutableMapOf<String, Any?>()
        map.put("transaction", transaction.toMap())
        map.put("shares", shares)
        return map
    }
}

data class TransactionToShareNamespace(val transaction: TransactionNamespace, val shares: Int)

class ShareTransactionList(val transactionsToShares: List<TransactionToShare>): BaseObject {
    override fun toMap(): MutableMap<String, Any?> {
        var map = mutableMapOf<String, Any?>()
        map.put("transactionsToShares", transactionsToShares.map { it.toMap() })
        return map
    }
}

data class ShareTransactionListNamespace(val transactionsToShares: List<TransactionToShareNamespace>)

class TransactionWithNewUser(
    val transactions: List<Transaction>,
    val newUser: NewUserAccount? = null
): BaseObject {
    override fun toMap(): MutableMap<String, Any?> {
        var map = mutableMapOf<String, Any?>()
        map.put("transactions", transactions.map { it.toMap() })
        map.put("newUser", newUser?.toMap() ?: "")
        return map
    }
}

data class TransactionWithNewUserNamespace(val transactions: List<TransactionNamespace>, val newUser: NewUserAccountNamespace? = null)