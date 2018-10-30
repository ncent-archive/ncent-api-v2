package main.services.challenge

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.Transaction

/**
 * Used to invalid a challenge
 * Check it's fees and rewards pools. If they are insufficient, invalidate the challenge
 */
class InvalidateChallengeService: SOAServiceInterface<Transaction> {
    override fun execute(caller: Int?, data: Any?, params: Map<String, String>?) : SOAResult<Transaction> {
        throw NotImplementedError()
    }
}