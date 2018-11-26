package main.services.reward

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.Transaction

/**
 * Transfer tokens based on rewards
 */
class DistributeRewardService: SOAServiceInterface<Transaction> {
    override fun execute(caller: Int?, data: Any?, params: Map<String, String>?) : SOAResult<Transaction> {
        // get the reward
        // get the transaction that is triggering this
        // validate the caller is internal (must come from challenge complete)
        // get the reward pool and distribute to chain based on type

        throw NotImplementedError()
    }
}