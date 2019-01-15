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
        from: String,
        to: String,
        amount: Double,
        name: String? = null,
        tokenId: Int? = null,
        previousTransactionId: Int? = null,
        notes: String? = null) : SOAResult<Transaction> {

        return TransferTokenHelper.transferToken(
                    from,
                    to,
                    amount,
                    ActionType.TRANSFER,
                    name,
                    tokenId,
                    previousTransactionId,
                    notes)
    }
}