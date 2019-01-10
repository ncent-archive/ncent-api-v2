package main.services.token

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import main.daos.Transaction
import main.helpers.TransferTokenHelper

/**
 * Transfer tokens from one address to another
 */
object TransferTokenService {
    fun execute(
        caller: UserAccount,
        from: String,
        to: String,
        name: String,
        amount: Double,
        previousTransactionId: Int?,
        notes: String?) : SOAResult<Transaction> {
        val address = caller.cryptoKeyPair.publicKey

        // verify that the caller is the from address
        if(address != from)
            return SOAResult(SOAResultType.FAILURE, "Access denied. Caller and from address must match.", null)

        return TransferTokenHelper.transferToken(
            caller,
            from,
            to,
            name,
            amount,
            ActionType.TRANSFER,
            previousTransactionId,
            notes
        )
    }
}