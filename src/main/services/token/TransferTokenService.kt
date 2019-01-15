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
        amount: Double,
        name: String? = null,
        tokenId: Int? = null,
        previousTransactionId: Int? = null,
        notes: String? = null) : SOAResult<Transaction> {
        val address = caller.cryptoKeyPair.publicKey

        // verify that the caller is the from address
        if(address != from)
            return SOAResult(SOAResultType.FAILURE, "Access denied. Caller and from address must match.", null)

        if (tokenId != null)
            return TransferTokenHelper.transferToken(
                    caller,
                    from,
                    to,
                    tokenId,
                    amount,
                    ActionType.TRANSFER,
                    previousTransactionId,
                    notes)
        else if (name != null)
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

        return SOAResult(SOAResultType.FAILURE, "Must include tokenId or token name.", null)
    }
}