package main.helpers

import framework.models.idValue
import framework.services.DaoService
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import main.services.token.GetTokenService
import main.services.transaction.GenerateTransactionService
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select

class TransferTokenHelper {

    fun transferToken(
        caller: Int?,
        from: String,
        to: String,
        tokenName: String,
        amount: Double,
        type: ActionType,
        previousTransactionId: Int?,
        notes: String?): SOAResult<Transaction> {
        // get the token type we wish to transfer
        val tokenResult = GetTokenService().execute(caller, tokenName)
        if(tokenResult.result != SOAResultType.SUCCESS)
            return SOAResult(tokenResult.result, tokenResult.message, null)

        return DaoService<SOAResult<Transaction>>().execute {
            val tokenId = tokenResult.data!!.tokenType.idValue
            // get the token transfer history for this address
            val callerTransferHistoryResult = getCallerTransferHistory(from, tokenId)
            if(callerTransferHistoryResult.result != SOAResultType.SUCCESS)
                return@execute SOAResult(callerTransferHistoryResult.result, callerTransferHistoryResult.message, null)

            // get and validate the users balance vs what they wish to transfer
            val callerBalance = calculateCallerBalance(from, callerTransferHistoryResult.data!!)
            if(callerBalance < amount)
                return@execute SOAResult(SOAResultType.FAILURE, "Insufficient funds", null)

            var metadataList = mutableListOf(MetadatasNamespace("amount", amount.toString()))
            if(notes != null)
                metadataList.add(MetadatasNamespace("notes", notes))

            // TODO validate that the to address exists -- maybe in generate transaction

            // generate a transaction moving funds
            val transactionNamespace = TransactionNamespace(
                from = from,
                to = to,
                action = ActionNamespace(
                    type = type,
                    data = tokenId,
                    dataType = Token::class.simpleName!!
                ),
                previousTransaction = previousTransactionId,
                metadatas = MetadatasListNamespace(metadataList)
            )
            return@execute GenerateTransactionService().execute(caller, transactionNamespace, null)
        }.data!!
    }

    // join from and to this caller and the token -- this will get the history of transfers
    // that this user was a part of for this particular token
    private fun getCallerTransferHistory(address: String, tokenId: Int): SOAResult<List<Transaction>> {
        return DaoService<List<Transaction>>().execute {
            val query = Transactions
                .innerJoin(Actions)
                .innerJoin(TransactionsMetadata)
                .innerJoin(Metadatas)
                .select {
                    (Transactions.from.eq(address) or Transactions.to.eq(address)) and
                        Actions.dataType.eq(Token::class.simpleName!!) and
                        Actions.data.eq(tokenId) and
                        Actions.type.eq(ActionType.TRANSFER)
                }.withDistinct()
        Transaction.wrapRows(query).toList().distinct()
        }
    }

    // calculate balance based on transfers
    fun calculateCallerBalance(address: String, transfers: List<Transaction>): Double {
        var balance = 0.0
        transfers.forEach { transfer ->
            if(transfer.from == address) {
                balance -= transfer.metadatas.find { it.key == "amount" }!!.value.toDouble()
            } else if(transfer.to == address) {
                balance += transfer.metadatas.find { it.key == "amount" }!!.value.toDouble()
            }
        }
        return balance
    }
}