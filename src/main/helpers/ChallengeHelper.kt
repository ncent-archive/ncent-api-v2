package main.helpers

import main.daos.Challenge
import main.daos.ChallengeToUnsharedTransactionsList
import main.daos.UserAccount
import main.services.challenge.GetChallengesService

object ChallengeHelper {
    fun findChallengeById(challengeId: Int?): Challenge {
        if (challengeId == null) {
            throw InternalError()
        }

        return Challenge.findById(challengeId)!!
    }

    fun getChallenges(user: UserAccount): ChallengeToUnsharedTransactionsList {
        return GetChallengesService.execute(user).data!!
    }
}