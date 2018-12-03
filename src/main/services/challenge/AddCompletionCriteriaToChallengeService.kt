package main.services.challenge

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.Transaction

/**
 * Add a completion criteria to a challenge.
 */
object AddCompletionCriteriaToChallengeService: SOAServiceInterface<Transaction> {
    override fun execute(caller: Int?, params: Map<String, String>?) : SOAResult<Transaction> {
        throw NotImplementedError()
    }
}