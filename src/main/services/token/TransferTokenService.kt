package main.services.token

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.Transaction

/**
 * Transfer tokens from one address to another
 */
class TransferTokenService: SOAServiceInterface<Transaction> {
    override fun execute(caller: Int?, data: Any?, params: Map<String, String>?) : SOAResult<Transaction> {
        // TODO: verify that the caller is the from address
        // TODO: verify they have enough funds and that the token itself is still valid
        // TODO: -- this can be done by getting all of the transactions for a user for a particular token
        // TODO: -- then calculating their balance
        // TODO: verify that the to address exists
        // TODO: Add a transaction transfering funds
        throw NotImplementedError()
    }
}