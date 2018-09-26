package main.challenges.services

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.challenges.models.Challenge

/**
 * Used to see if a challenge is valid
 */
class ValidateChallengeService: SOAServiceInterface<Challenge> {
    override fun execute(caller: Int?, data: Challenge?, params: Map<String, String>?) : SOAResult<Challenge> {
        throw NotImplementedError()
    }
}