package main.challenges.services

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.challenges.models.Challenge
import main.transactions.models.Action
import main.transactions.models.Transaction

/**
 * Check that the challenge is expired, add an expiration transaction
 */
class ExpireChallengeService: SOAServiceInterface<Transaction<Action<Challenge>>> {
    override fun execute(caller: Int?, data: Transaction<Action<Challenge>>?, params: Map<String, String>?) : SOAResult<Transaction<Action<Challenge>>> {
        throw NotImplementedError()
    }
}