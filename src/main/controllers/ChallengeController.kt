package main.controllers

import com.beust.klaxon.JsonObject
import kotlinserverless.framework.controllers.RestController
import kotlinserverless.framework.controllers.DefaultController
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import main.services.challenge.*
import main.helpers.JsonHelper
import main.helpers.ChallengeHelper
import main.helpers.ControllerHelper.RequestData

class ChallengeController: DefaultController<Challenge>(), RestController<Challenge, UserAccount> {
    override fun create(user: UserAccount?, requestData: RequestData): SOAResult<Challenge> {
        val challengeNamespace = JsonHelper.parse<ChallengeNamespace>(requestData.body["challengeNamespace"]!! as JsonObject)

        return GenerateChallengeService.execute(user!!, challengeNamespace)
    }

    override fun findOne(user: UserAccount, requestData: RequestData, id: Int?): SOAResult<Challenge> {
        val challenge = ChallengeHelper.findChallengeById(id)

        return SOAResult(SOAResultType.SUCCESS, "", challenge)
    }

    override fun findAll(user: UserAccount, requestData: RequestData): SOAResult<List<Challenge>> {
        val challenges = ChallengeHelper.getChallenges(user)

        return SOAResult(SOAResultType.SUCCESS, null, challenges.challengeToUnsharedTransactions.map { it.challenge })
    }

    fun complete(user: UserAccount?, requestData: RequestData): SOAResult<TransactionList> {
        val completerPublicKey = requestData.body["completerPublicKey"] as String
        val challengeId = requestData.body["challengeId"] as Int
        val challenge = ChallengeHelper.findChallengeById(challengeId)

        return CompleteChallengeService.execute(user!!, challenge, completerPublicKey)
    }

    fun redeem(user: UserAccount?, requestData: RequestData): SOAResult<TransactionList> {
        val completerPublicKey = requestData.body["completerPublicKey"] as String
        val challengeId = requestData.body["challengeId"] as Int
        val challenge = ChallengeHelper.findChallengeById(challengeId)

        return RedeemChallengeService.execute(user!!, challenge, completerPublicKey)
    }

    fun share(user: UserAccount?, requestData: RequestData): SOAResult<TransactionWithNewUser> {
        val challengeId = requestData.body["challengeId"] as String
        val publicKeyToShareWith = requestData.body["publicKeyToShareWith"] as String?
        val shares = requestData.body["shares"] as Int
        val expiration = requestData.body["expiration"] as String?
        val emailToShareWith = requestData.body["emailToShareWith"] as String?

        val challenge = ChallengeHelper.findChallengeById(challengeId.toInt())

        return ShareChallengeService.execute(user!!, challenge, shares, publicKeyToShareWith, emailToShareWith, expiration)
    }

    fun balances(user: UserAccount?, requestData: RequestData): SOAResult<EmailToChallengeBalanceList> {
        val challengeId = requestData.queryParams["challengeId"] as String

        return GetAllBalancesForChallengeService.execute(user!!, challengeId.toInt())
    }
}