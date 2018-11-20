package main.services.token

import framework.models.idValue
import framework.services.DaoService
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.*
import main.daos.Transaction
import main.services.transaction.GenerateTransactionService
import org.jetbrains.exposed.sql.*

/**
 * Transfer tokens from one address to another
 */
class TransferTokenService: SOAServiceInterface<Transaction> {
    override fun execute(caller: Int?, params: Map<String, String>?) : SOAResult<Transaction> {
        val userAccount = UserAccount.findById(caller!!)
        // get the user account so we can get the address
        if(userAccount == null)
            return SOAResult(SOAResultType.FAILURE, "Could not find that user", null)
        val address = userAccount!!.cryptoKeyPair.publicKey

        // verify that the caller is the from address
        if(address != params!!["from"])
            return SOAResult(SOAResultType.FAILURE, "Access denied. Caller and from address must match.", null)

        // get the token type we wish to transfer
        val tokenResult = GetTokenService().execute(caller, params!!["name"])
        if(tokenResult.result != SOAResultType.SUCCESS)
            return SOAResult(tokenResult.result, tokenResult.message, null)

        return DaoService<SOAResult<Transaction>>().execute {
            val tokenId = tokenResult.data!!.tokenType.idValue
            // get the token transfer history for this address
            val callerTransferHistoryResult = getCallerTransferHistory(address, tokenId)
            if(callerTransferHistoryResult.result != SOAResultType.SUCCESS)
                return@execute SOAResult(callerTransferHistoryResult.result, callerTransferHistoryResult.message, null)

            // get and validate the users balance vs what they wish to transfer
            val callerBalance = calculateCallerBalance(address, callerTransferHistoryResult.data!!)
            if(callerBalance < Integer.valueOf(params!!["amount"]))
                return@execute SOAResult(SOAResultType.FAILURE, "Insufficient funds", null)

            var metadataList = mutableListOf(MetadatasNamespace("amount", params!!["amount"]!!))
            if(params!!["notes"] != null)
                metadataList.add(MetadatasNamespace("notes", params!!["notes"]!!))

            // TODO validate that the to address exists -- maybe in generate transaction

            // generate a transaction moving funds
            val transactionNamespace = TransactionNamespace(
                from = address,
                to = params!!["to"],
                action = ActionNamespace(
                    type = ActionType.TRANSFER,
                    data = tokenId,
                    dataType = Token::class.simpleName!!
                ),
                previousTransaction = null,
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
    private fun calculateCallerBalance(address: String, transfers: List<Transaction>): Double {
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