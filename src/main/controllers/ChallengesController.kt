package main.controllers

import kotlinserverless.framework.controllers.RestController
import kotlinserverless.framework.controllers.DefaultController
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import main.helpers.ChallengeHelper
import main.helpers.ControllerHelper.RequestData

class ChallengesController: DefaultController<Challenge>(), RestController<ChallengeList, UserAccount> {
    override fun findOne(user: UserAccount, requestData: RequestData, id: Int?): SOAResult<ChallengeList> {
        validateApiKey(user, requestData)

        val challengesResult = ChallengeHelper.getChallenges(user)

        if (challengesResult.data == null) {
            throw InternalError()
        }

        return SOAResult(SOAResultType.SUCCESS, challengesResult.message, ChallengeList(challengesResult.data!!.challengeToUnsharedTransactions.map { it -> it.challenge }))
    }
}