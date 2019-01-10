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
object GetTransactionsService: SOAServiceInterface<TransactionList> {
    override fun execute(params: Map<String, String>) : SOAResult<TransactionList> {
        var actionIds: List<Int>? = null
        if(params["dataType"] != null) {
            val query = Actions.selectAll()
            params["data"]?.let {
                query.andWhere { Actions.data eq Integer.valueOf(params["data"]!!) }
            }
            params["type"]?.let {
                query.andWhere { Actions.type eq ActionType.valueOf(params["type"]!!) }
            }
            params["dataType"]?.let {
                query.andWhere { Actions.dataType eq params["dataType"]!! }
            }
            actionIds = query.map { it[Actions.id].value }
        }

        val query = Transactions.selectAll()
        actionIds?.let {
            query.andWhere { Transactions.action inList actionIds }
        }
        params["previousTransaction"]?.let {
            query.andWhere { Transactions.previousTransaction eq EntityID(
                Integer.valueOf(params["previousTransaction"]!!),
                Transactions
            ) }
        }
        params["from"]?.let {
            query.andWhere { Transactions.from eq params["from"]!! }
        }
        params["to"]?.let {
            query.andWhere { Transactions.to eq params["to"]!! }
        }
        query.withDistinct()
        // TODO figure out how to do this without a seprate query
        return return SOAResult(SOAResultType.SUCCESS, null, TransactionList(Transaction.find {
            Transactions.id inList query.map { it[Transactions.id] }
        }.toList().reversed()))
    }
}