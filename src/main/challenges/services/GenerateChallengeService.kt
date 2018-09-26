package main.challenges.services

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.challenges.models.Challenge

/**
 * Create a challenge; generate all appropriate objects including transaction(s)
 */
class GenerateChallengeService: SOAServiceInterface<Challenge> {
    override fun execute(caller: Int?, data: Challenge?, params: Map<String, String>?) : SOAResult<Challenge> {
        throw NotImplementedError()
    }
}