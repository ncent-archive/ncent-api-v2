package main.challenges.services

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.challenges.models.Challenge
import main.transactions.models.Action
import main.transactions.models.Transaction

/**
 * Used to invalid a challenge
 * Check it's fees and rewards pools. If they are insufficient, invalidate the challenge
 */
class InvalidateChallengeService: SOAServiceInterface<Transaction<Action<Challenge>>> {
    override fun execute(caller: Int?, data: Transaction<Action<Challenge>>?, params: Map<String, String>?) : SOAResult<Transaction<Action<Challenge>>> {
        throw NotImplementedError()
    }
}