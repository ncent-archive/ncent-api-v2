package main.rewards.services

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.rewards.models.Reward

/**
 * Generate a reward if it is valid
 */
class GenerateRewardService: SOAServiceInterface<Reward> {
    override fun execute(caller: Int?, data: Reward?, params: Map<String, String>?) : SOAResult<Reward> {
        throw NotImplementedError()
    }
}