package main.services.reward

import kotlinserverless.framework.services.SOAResult
import main.daos.Reward
import main.daos.Transaction
import main.daos.UserAccount
import main.services.token.TransferTokenService

/**
 * Transfer tokens to reward pool
 */
object AddToRewardPoolService {
    fun execute(caller: UserAccount, rewardId: Int, name: String, amount: Double) : SOAResult<Transaction> {
        val reward = Reward.findById(rewardId)!!
        return TransferTokenService.execute(
                caller.cryptoKeyPair.publicKey,
                reward.pool!!.cryptoKeyPair.publicKey,
                amount,
                name
        )
    }
}