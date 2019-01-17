package main.helpers

import framework.services.DaoService
import kotlinserverless.framework.models.NotFoundException
import kotlinserverless.framework.services.SOAResultType
import main.daos.Challenge

object ChallengeHelper {
    @Throws(NotFoundException::class)
    fun findChallengeById(challengeId: Int?): Challenge {
        if (challengeId == null) {
            throw InternalError()
        }

        val findChallengeResult = DaoService.execute {
            Challenge.findById(challengeId)
        }

        if (findChallengeResult.result != SOAResultType.SUCCESS || findChallengeResult.data == null) {
            throw NotFoundException("Challenge not found")
        }

        return findChallengeResult.data!!
    }
}