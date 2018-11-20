package main.services.token

import framework.models.idValue
import framework.services.DaoService
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.*
import main.services.transaction.GenerateTransactionService

/**
 * Transfer tokens from one address to another
 */
class TransferTokenService: SOAServiceInterface<Transaction> {
    override fun execute(caller: Int?, data: Any?, params: Map<String, String>?) : SOAResult<Transaction> {
        // TODO: verify they have enough funds and that the token itself is still valid
        // TODO: -- this can be done by getting all of the transactions for a user for a particular token
        // TODO: -- then calculating their balance
        // TODO: verify that the to address exists
        // TODO: Add a transaction transfering funds

        // verify that the caller is the from address
        if(caller!! != Integer.valueOf(params!!["from"]))
            return SOAResult(SOAResultType.FAILURE, "Access denied. Caller and from address must match.", null)

        val tokenResult = GetTokenService().execute(caller, params!!["name"])
        if(tokenResult.result != SOAResultType.SUCCESS)
            return SOAResult(tokenResult.result, tokenResult.message, null)

        return DaoService<SOAResult<Transaction>>().execute {
            val tokenId = tokenResult.data!!.tokenType.idValue
            val callerTransferHistoryResult = getCallerTransferHistory(caller!!, tokenId)
            if(callerTransferHistoryResult.result != SOAResultType.SUCCESS)
                return@execute SOAResult(callerTransferHistoryResult.result, callerTransferHistoryResult.message, null)

            val callerBalance = calculateCallerBalance(callerTransferHistoryResult.data!!)
            if(callerBalance < Integer.valueOf(params!!["amount"]))
                return@execute SOAResult(SOAResultType.FAILURE, "Insufficient funds", null)
            val metadatas = if(params!!["notes"] != null) {
                MetadatasListNamespace(listOf(MetadatasNamespace("notes", params!!["notes"]!!)))
            } else
                null
            val transactionNamespace = TransactionNamespace(
                from = caller!!.toString(),
                to = params!!["to"],
                action = ActionNamespace(
                    type = ActionType.TRANSFER,
                    data = tokenId,
                    dataType = "Token"
                ),
                previousTransaction = null,
                metadatas = metadatas
            )
            return@execute GenerateTransactionService().execute(caller, transactionNamespace, null)
        }.data!!
    }

    // join from and to this caller
    fun getCallerTransferHistory(caller: Int, tokenId: Int): SOAResult<List<Transaction>> {

    }

    fun calculateCallerBalance(transfers: List<Transaction>): Double {

    }
}