package main.rewards.services

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.rewards.models.Reward
import main.transactions.models.Transaction

/**
 * Transfer tokens based on rewards
 */
class DistributeRewardService: SOAServiceInterface<Transaction> {
    override fun execute(caller: Int?, data: Transaction?, params: Map<String, String>?) : SOAResult<Transaction> {
        throw NotImplementedError()
    }
}