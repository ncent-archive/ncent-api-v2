package main.services.challenge

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.Challenge

/**
 * Used to see if a challenge is valid
 */
class ValidateChallengeService: SOAServiceInterface<Challenge> {
    override fun execute(caller: Int?, data: Any?, params: Map<String, String>?) : SOAResult<Challenge> {
        throw NotImplementedError()
    }
}