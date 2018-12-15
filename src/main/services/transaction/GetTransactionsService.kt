package main.services.transaction

import framework.services.DaoService
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.*
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.selectAll

/**
 * Retrieve transactions by filter, such as from/to
 * "to" is required for now. need at least one field required
 */
object GetTransactionsService: SOAServiceInterface<TransactionList> {
    override fun execute(caller: Int?, params: Map<String, String>?) : SOAResult<TransactionList> {
        var actionIds: List<Int>? = null
        if(params!!["dataType"] != null) {
            val actionResult = DaoService.execute {
                val query = Actions.selectAll()
                params!!["data"]?.let {
                    query.andWhere { Actions.data eq Integer.valueOf(params!!["data"]!!) }
                }
                params!!["type"]?.let {
                    query.andWhere { Actions.type eq ActionType.valueOf(params!!["type"]!!) }
                }
                params!!["dataType"]?.let {
                    query.andWhere { Actions.dataType eq params!!["dataType"]!! }
                }
                query.map { it[Actions.id].value }
            }
            if(actionResult.result != SOAResultType.SUCCESS)
                return SOAResult(actionResult.result, actionResult.message, null)
            actionIds = actionResult.data!!
        }

        val transactionsResult = DaoService.execute {
            val query = Transactions.selectAll()
            actionIds?.let {
                query.andWhere { Transactions.action inList actionIds }
            }
            params!!["previousTransaction"]?.let {
                query.andWhere { Transactions.previousTransaction eq EntityID(
                    Integer.valueOf(params!!["previousTransaction"]!!),
                    Transactions
                ) }
            }
            params!!["from"]?.let {
                query.andWhere { Transactions.from eq params!!["from"]!! }
            }
            params!!["to"]?.let {
                query.andWhere { Transactions.to eq params!!["to"]!! }
            }
            query.withDistinct()
            // TODO figure out how to do this without a seprate query
            Transaction.find {
                Transactions.id inList query.map { it[Transactions.id] }
            }.toList().reversed()
        }

        if(transactionsResult.result == SOAResultType.SUCCESS)
            return SOAResult(SOAResultType.SUCCESS, null, TransactionList(transactionsResult.data!!))
        return SOAResult(transactionsResult.result, transactionsResult.message, null)
    }
}