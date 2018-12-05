package main.services.challenge

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.ChallengeList

/**
 * Retrieve a single providence chain.
 * The transaction queried must NOT have children and must be a leaf node
 *
 */
object GetChallengesService: SOAServiceInterface<ChallengeList> {
    override fun execute(caller: Int?, id: Int?): SOAResult<ChallengeList> {
        throw NotImplementedError()
    }
}