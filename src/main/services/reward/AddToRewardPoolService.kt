package main.services.reward

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.Reward
import main.daos.Transaction
import main.daos.UserAccount
import main.services.token.TransferTokenService

/**
 * Transfer tokens to reward pool
 */
object AddToRewardPoolService: SOAServiceInterface<Transaction> {
    override fun execute(caller: Int?, params: Map<String, String>?) : SOAResult<Transaction> {
        val userAccount = UserAccount.findById(caller!!)!!
        val reward = Reward.findById(params!!["reward_id"]!!.toInt())!!
        return TransferTokenService.execute(caller, mapOf(
            Pair("to", reward.pool!!.cryptoKeyPair.publicKey),
            Pair("from", userAccount.cryptoKeyPair.publicKey),
            Pair("name", params!!["name"]!!),
            Pair("amount", params!!["amount"]!!)
        ))
    }
}