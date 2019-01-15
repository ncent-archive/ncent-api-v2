package main.helpers

import framework.models.idValue
import framework.services.DaoService
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import main.services.token.GetTokenService
import main.services.transaction.GenerateTransactionService
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select

object TransferTokenHelper {

    fun transferToken(
        from: String,
        to: String,
        amount: Double,
        type: ActionType,
        name: String? = null,
        id: Int? = null,
        previousTransactionId: Int? = null,
        notes: String? = null): SOAResult<Transaction> {

        var tokenId: Int
        if (id == null && name == null)
            return SOAResult(SOAResultType.FAILURE, "Must include either a token name or tokenId", null)
        else if(id == null) {
            val tokenResult = GetTokenService.execute(name!!)
            if (tokenResult.result != SOAResultType.SUCCESS)
                return SOAResult(tokenResult.result, tokenResult.message, null)
            tokenId = tokenResult.data!!.tokenType.idValue
        } else {tokenId = id}

        return DaoService.execute {
            // get the token transfer history for this address
            val callerTransferHistoryResult = getTransferHistory(from, tokenId)
            if(callerTransferHistoryResult.result != SOAResultType.SUCCESS)
                return@execute SOAResult<Transaction>(callerTransferHistoryResult.result, callerTransferHistoryResult.message, null)

            // get and validate the users balance vs what they wish to transfer
            val callerBalance = calculateBalance(from, callerTransferHistoryResult.data!!)
            if(callerBalance < amount)
                return@execute SOAResult<Transaction>(SOAResultType.FAILURE, "Insufficient funds", null)

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
            return@execute GenerateTransactionService.execute(transactionNamespace)
        }.data!!
    }

    // join from and to this caller and the token -- this will get the history of transfers
    // that this user was a part of for this particular token
    fun getTransferHistory(address: String, tokenId: Int? = null): SOAResult<List<Transaction>> {
        return DaoService.execute {
            val expression = if(tokenId != null) {
                (Transactions.from.eq(address) or Transactions.to.eq(address)) and
                    Actions.dataType.eq(Token::class.simpleName!!) and
                    Actions.data.eq(tokenId) and
                    Actions.type.eq(ActionType.TRANSFER)
            } else {
                (Transactions.from.eq(address) or Transactions.to.eq(address)) and
                    Actions.dataType.eq(Token::class.simpleName!!) and
                    Actions.type.eq(ActionType.TRANSFER)
            }
            val query = Transactions
                .innerJoin(Actions)
                .innerJoin(TransactionsMetadata)
                .innerJoin(Metadatas)
                .select {
                    expression
                }.withDistinct()
        Transaction.wrapRows(query).toList().distinct()
        }
    }

    // calculate balance based on transfers
    // must pass list of transactions for a particular currency only
    fun calculateBalance(address: String, transfers: List<Transaction>): Double {
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

    fun getMapOfTransfersByCurrency(transfers: List<Transaction>): Map<Int, MutableList<Transaction>> {
        var currencyToTransactions = mutableMapOf<Int, MutableList<Transaction>>()
        transfers.forEach { transaction ->
            currencyToTransactions.putIfAbsent(transaction.action.data, mutableListOf())
            currencyToTransactions[transaction.action.data]!!.add(transaction)
        }
        return currencyToTransactions
    }

    fun getMapOfBalancesByCurrency(address: String): SOAResult<Map<Int, Double>> {
        val transactionsResult = getTransferHistory(address, null)
        if(transactionsResult.result != SOAResultType.SUCCESS)
            return SOAResult(SOAResultType.FAILURE, transactionsResult.message)
        val mapOfTransfers = getMapOfTransfersByCurrency(transactionsResult.data!!)

        var currencyToBalances = mutableMapOf<Int, Double>()
        mapOfTransfers.forEach { currency_id, transactions ->
            currencyToBalances.putIfAbsent(currency_id, 0.0)
            currencyToBalances[currency_id] =
                    currencyToBalances[currency_id]!! + calculateBalance(address, transactions)
        }
        return SOAResult(
                SOAResultType.SUCCESS,
                "Map of balances retrieved.",
                currencyToBalances)
    }
}