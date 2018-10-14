package main.services.challenge

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.Challenge

/**
 * Create a challenge; generate all appropriate objects including transaction(s)
 */
class GenerateChallengeService: SOAServiceInterface<Challenge> {
    override fun execute(caller: Int?, data: Challenge?, params: Map<String, String>?) : SOAResult<Challenge> {
        throw NotImplementedError()
    }
}