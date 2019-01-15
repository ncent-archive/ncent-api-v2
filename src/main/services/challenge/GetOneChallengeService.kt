package main.services.challenge

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import framework.services.DaoService

object GetOneChallengeService {
    fun execute(challengeId: Int): SOAResult<Challenge> {
        val challengeResult = DaoService.execute {
            Challenge.findById(challengeId)
        }

        if (challengeResult.data == null) {
            return SOAResult(SOAResultType.FAILURE, "Challenge with id $challengeId not found", null)
        } else if (challengeResult.result != SOAResultType.SUCCESS) {
            return SOAResult(challengeResult.result, challengeResult.message, null)
        }
        return SOAResult(SOAResultType.SUCCESS, null, challengeResult.data!!)
    }
}