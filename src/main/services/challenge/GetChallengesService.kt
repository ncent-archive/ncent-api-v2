package main.services.challenge

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.ChallengeList

/**
 *
 * Retrieve one or more challenges based on filters
 *
 */
object GetChallengesService: SOAServiceInterface<ChallengeList> {

    // get challenges by filter
    override fun execute(caller: Int?, params: Map<String, String>?): SOAResult<ChallengeList> {
        throw NotImplementedError()
    }

    // get challenges for a caller
    override fun execute(caller: Int?): SOAResult<ChallengeList> {
        throw NotImplementedError()
    }
}