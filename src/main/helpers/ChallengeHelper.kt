package main.helpers

import framework.services.DaoService
import kotlinserverless.framework.models.NotFoundException
import kotlinserverless.framework.services.SOAResult
import main.daos.Challenge
import main.daos.ChallengeToUnsharedTransactionsList
import main.daos.UserAccount
import main.services.challenge.GetChallengesService

object ChallengeHelper {
    @Throws(NotFoundException::class)
    fun findChallengeById(challengeId: Int?): Challenge {
        if (challengeId == null) {
            throw InternalError()
        }

        return Challenge.findById(challengeId)!!
    }

    @Throws(NotFoundException::class)
    fun getChallenges(user: UserAccount): SOAResult<ChallengeToUnsharedTransactionsList?> {
        val getChallengesResult = DaoService.execute {
            GetChallengesService.execute(user).data
        }

        if (getChallengesResult.data == null || getChallengesResult.data?.challengeToUnsharedTransactions?.isEmpty() == true) {
            throw NotFoundException("No challenges found")
        }

        return getChallengesResult
    }
}