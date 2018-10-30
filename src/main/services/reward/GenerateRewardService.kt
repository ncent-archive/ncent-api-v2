package main.services.reward

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.Reward

/**
 * Generate a reward if it is valid
 */
class GenerateRewardService: SOAServiceInterface<Reward> {
    override fun execute(caller: Int?, data: Any?, params: Map<String, String>?) : SOAResult<Reward> {
        throw NotImplementedError()
    }
}