package main.services.token

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import main.daos.Transaction
import main.helpers.TransferTokenHelper

/**
 * Transfer tokens from one address to another
 */
object TransferTokenService: SOAServiceInterface<Transaction> {
    override fun execute(caller: UserAccount, params: Map<String, String>?) : SOAResult<Transaction> {
        val address = caller.cryptoKeyPair.publicKey

        // verify that the caller is the from address
        if(address != params!!["from"])
            return SOAResult(SOAResultType.FAILURE, "Access denied. Caller and from address must match.", null)

        return TransferTokenHelper.transferToken(
            caller,
            params!!["from"]!!,
            params!!["to"]!!,
            params!!["name"]!!,
            params!!["amount"]!!.toDouble(),
            ActionType.TRANSFER,
            null,
            params!!["notes"]
        )
    }
}