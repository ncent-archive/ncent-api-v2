package main.services.transaction

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.selectAll

/**
 * Retrieve transactions by filter, such as from/to
 * "to" is required for now. need at least one field required
 */
object GetTransactionsService {
    fun execute(
        from: String?,
        to: String?,
        previousTxId: Int?,
        actionNamespace: ActionNamespace?
    ) : SOAResult<TransactionList> {
        var actionIds: List<Int>? = null
        if(actionNamespace != null) {
            val query = Actions.selectAll()
            actionNamespace.data?.let {
                query.andWhere { Actions.data eq actionNamespace.data }
            }
            actionNamespace.type?.let {
                query.andWhere { Actions.type eq actionNamespace.type }
            }
            actionNamespace.dataType?.let {
                query.andWhere { Actions.dataType eq actionNamespace.dataType }
            }
            actionIds = query.map { it[Actions.id].value }
        }

        val query = Transactions.selectAll()
        actionIds?.let {
            query.andWhere { Transactions.action inList actionIds }
        }
        previousTxId?.let {
            query.andWhere { Transactions.previousTransaction eq EntityID(
                previousTxId,
                Transactions
            ) }
        }
        from?.let {
            query.andWhere { Transactions.from eq from }
        }
        to?.let {
            query.andWhere { Transactions.to eq to }
        }
        query.withDistinct()
        // TODO figure out how to do this without a seprate query
        return return SOAResult(SOAResultType.SUCCESS, null, TransactionList(Transaction.find {
            Transactions.id inList query.map { it[Transactions.id] }
        }.toList().reversed()))
    }
}