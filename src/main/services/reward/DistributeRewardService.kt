package main.services.reward

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.Transaction

/**
 * Transfer tokens based on rewards
 */
class DistributeRewardService: SOAServiceInterface<Transaction> {
    override fun execute(caller: Int?, data: Transaction?, params: Map<String, String>?) : SOAResult<Transaction> {
        throw NotImplementedError()
    }
}