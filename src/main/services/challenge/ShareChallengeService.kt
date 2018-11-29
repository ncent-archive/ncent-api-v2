package main.services.challenge

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.Transaction

/**
 * Share a challenge.
 */
object ShareChallengeService: SOAServiceInterface<Transaction> {
    override fun execute(caller: Int?, params: Map<String, String>?) : SOAResult<Transaction> {
        throw NotImplementedError()
    }
}