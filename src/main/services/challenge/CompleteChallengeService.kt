package main.services.challenge

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.Transaction

/**
 * Validate and complete the challenge based on the completion of challenge transaction passed.
 */
class CompleteChallengeService: SOAServiceInterface<Transaction> {
    override fun execute(caller: Int?, data: Any?, params: Map<String, String>?) : SOAResult<Transaction> {
        throw NotImplementedError()
    }
}