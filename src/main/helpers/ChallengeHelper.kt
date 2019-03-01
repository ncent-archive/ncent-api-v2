package main.helpers

import framework.services.DaoService
import kotlinserverless.framework.models.NotFoundException
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
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

        val findChallengeResult = DaoService.execute {
            Challenge.findById(challengeId)
        }

        if (findChallengeResult.result != SOAResultType.SUCCESS || findChallengeResult.data == null) {
            throw NotFoundException("Challenge not found")
        }

        return findChallengeResult.data!!
    }

    @Throws(NotFoundException::class)
    fun getChallenges(user: UserAccount): SOAResult<ChallengeToUnsharedTransactionsList?> {
        val getChallengesResult = DaoService.execute {
            GetChallengesService.execute(user).data
        }

        if (getChallengesResult.data == null || getChallengesResult.data?.challengeToUnsharedTransactions?.isEmpty() == true) {
            throw NotFoundException(getChallengesResult.message ?: "No challenges found")
        }

        return getChallengesResult
    }
}