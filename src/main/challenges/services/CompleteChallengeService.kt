package main.challenges.services

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.challenges.models.Challenge
import main.transactions.models.Action
import main.transactions.models.Transaction

/**
 * Validate and complete the challenge based on the completion of challenge transaction passed.
 */
class CompleteChallengeService: SOAServiceInterface<Transaction<Action<Challenge>>> {
    override fun execute(caller: Int?, data: Transaction<Action<Challenge>>?, params: Map<String, String>?) : SOAResult<Transaction<Action<Challenge>>> {
        throw NotImplementedError()
    }
}