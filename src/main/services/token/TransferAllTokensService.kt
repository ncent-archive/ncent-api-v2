package main.services.token

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import main.daos.Transaction
import main.helpers.TransferTokenHelper
import main.services.reward.DistributeRewardService

/**
 * Transfer all tokens held by one address to another
 */
object TransferAllTokensService {
    fun execute(
            caller: UserAccount,
            from: String,
            to: String,
            notes: String?) : SOAResult<TransactionList> {
        val address = caller.cryptoKeyPair.publicKey

        // verify that the caller is the from address
        if(address != from)
            return SOAResult(
                    SOAResultType.FAILURE,
                    "Access denied. Caller and from address must match.",
                    null)

        // produce list of all tokenIds with a balance
        val transactionsResult = TransferTokenHelper.getTransferHistory(address, null)
        if(transactionsResult.result != SOAResultType.SUCCESS)
            return SOAResult(SOAResultType.FAILURE, transactionsResult.message)
        val mapOfTransfers = TransferTokenHelper.getMapOfTransfersByCurrency(transactionsResult.data!!)
        val mapOfBalances = TransferTokenHelper.getMapOfBalancesByCurrency(address, mapOfTransfers)

        var resultingTransactions = mutableListOf<Transaction>()
        var result : SOAResult<TransactionList> =
                SOAResult(SOAResultType.SUCCESS, "All tokens transferred successfully.", null)

        if(mapOfBalances.size == 0)
            result.message = "Address has no associated balances so no tokens were transferred."

        // Transfer all tokens to the new address
        mapOfBalances.forEach { tokenId, balance ->
            if(balance <= 0.0 || result.result != SOAResultType.SUCCESS)
                return@forEach
            val transferResult : SOAResult<Transaction> = TransferTokenService.execute(
                    caller,
                    from,
                    to,
                    tokenId,
                    balance,
                    null,
                    notes)
            resultingTransactions.add(transferResult.data!!)
            if(transferResult.result != SOAResultType.SUCCESS) {
                result.result = transferResult.result
                result.message = transferResult.message
            }
        }
        result.data = TransactionList(resultingTransactions)
        return result
    }
}