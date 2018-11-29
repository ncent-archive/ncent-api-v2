package main.services.challenge

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.Transaction

/**
 * Check that the challenge is expired, add an expiration transaction
 */
object ExpireChallengeService: SOAServiceInterface<Transaction> {
    override fun execute(caller: Int?, data: Any?, params: Map<String, String>?) : SOAResult<Transaction> {
        throw NotImplementedError()
    }
}