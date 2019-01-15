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
            toAddress: String,
            notes: String? = null) : SOAResult<TransactionList> {
        val fromAddress = caller.cryptoKeyPair.publicKey

        // Generate map of all tokenIds with a balance
        val transactionsResult = TransferTokenHelper.getTransferHistory(fromAddress, null)
        if(transactionsResult.result != SOAResultType.SUCCESS)
            return SOAResult(SOAResultType.FAILURE, transactionsResult.message)
        val mapOfTransfers = TransferTokenHelper.getMapOfTransfersByCurrency(transactionsResult.data!!)
        val mapOfBalances = TransferTokenHelper.getMapOfBalancesByCurrency(fromAddress, mapOfTransfers)


        // Transfer all tokens to the new address
        var resultingTransactions = mutableListOf<Transaction>()
        var result : SOAResult<TransactionList> =
                SOAResult(SOAResultType.SUCCESS, "All tokens transferred successfully.", null)

        if(mapOfBalances.size == 0)
            result.message = "Address has no associated balances so no tokens were transferred."

        mapOfBalances.forEach { tokenId, balance ->
            if(result.result != SOAResultType.SUCCESS)
                return@forEach
            if(balance <= 0.0) {
                result.result = SOAResultType.FAILURE
                result.message = "Non zero balance found for token type $tokenId"
                return@forEach
            }
            val transferResult : SOAResult<Transaction> = TransferTokenService.execute(
                    caller,
                    fromAddress,
                    toAddress,
                    balance,
                    null,
                    tokenId,
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