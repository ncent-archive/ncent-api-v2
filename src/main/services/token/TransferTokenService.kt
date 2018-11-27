package main.services.token

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.*
import main.daos.Transaction
import main.helpers.TransferTokenHelper

/**
 * Transfer tokens from one address to another
 */
class TransferTokenService: SOAServiceInterface<Transaction> {
    private val transferTokenHelper = TransferTokenHelper()

    override fun execute(caller: Int?, params: Map<String, String>?) : SOAResult<Transaction> {
        val userAccount = UserAccount.findById(caller!!)
        // get the user account so we can get the address
        if(userAccount == null)
            return SOAResult(SOAResultType.FAILURE, "Could not find that user", null)
        val address = userAccount!!.cryptoKeyPair.publicKey

        // verify that the caller is the from address
        if(address != params!!["from"])
            return SOAResult(SOAResultType.FAILURE, "Access denied. Caller and from address must match.", null)

        return transferTokenHelper.transferToken(
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