package main.controllers

import com.beust.klaxon.JsonObject
import framework.services.DaoService
import kotlinserverless.framework.controllers.RestController
import kotlinserverless.framework.controllers.DefaultController
import kotlinserverless.framework.models.ForbiddenException
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import main.services.challenge.*
import main.helpers.JsonHelper
import main.helpers.ChallengeHelper
import main.helpers.ControllerHelper.RequestData

class ChallengeController: DefaultController<Challenge>(), RestController<Challenge, UserAccount> {
    override fun create(user: UserAccount?, requestData: RequestData): SOAResult<Challenge> {
        val generateChallengeResult = DaoService.execute {
            val challengeNamespace = JsonHelper.parse<ChallengeNamespace>(requestData.body["challengeNamespace"]!! as JsonObject)

            return@execute GenerateChallengeService.execute(user!!, challengeNamespace)
        }
        DaoService.throwOrReturn(generateChallengeResult)
        return generateChallengeResult.data!!
    }

    override fun findOne(user: UserAccount, requestData: RequestData, id: Int?): SOAResult<Challenge> {
        val challenge = ChallengeHelper.findChallengeById(id)

        return SOAResult(SOAResultType.SUCCESS, "", challenge)
    }

    override fun findAll(user: UserAccount, requestData: RequestData): SOAResult<List<Challenge>> {
        val challengesResult = ChallengeHelper.getChallenges(user)

        if (challengesResult.data == null) {
            throw InternalError()
        }

        return SOAResult(SOAResultType.SUCCESS, challengesResult.message, challengesResult.data!!.challengeToUnsharedTransactions.map { it -> it.challenge })
    }

    fun complete(user: UserAccount?, requestData: RequestData): SOAResult<TransactionList> {
        val completerPublicKey = requestData.body["completerPublicKey"] as String
        val challengeId = requestData.body["challengeId"] as Int
        val challenge = ChallengeHelper.findChallengeById(challengeId)

        val completeResult = DaoService.execute {
            return@execute CompleteChallengeService.execute(user!!, challenge, completerPublicKey)
        }

        DaoService.throwOrReturn(completeResult)
        return completeResult.data!!
    }

    @Throws(ForbiddenException::class)
    fun redeem(user: UserAccount?, requestData: RequestData): SOAResult<TransactionList> {
        val completerPublicKey = requestData.body["completerPublicKey"] as String
        val challengeId = requestData.body["challengeId"] as Int
        val challenge = ChallengeHelper.findChallengeById(challengeId)

        val redeemResult = DaoService.execute {
            RedeemChallengeService.execute(user!!, challenge, completerPublicKey)
        }
        DaoService.throwOrReturn(redeemResult)
        return redeemResult.data!!
    }

    @Throws(ForbiddenException::class)
    fun share(user: UserAccount?, requestData: RequestData): SOAResult<TransactionWithNewUser> {
        val challengeId = requestData.body["challengeId"] as String
        val publicKeyToShareWith = requestData.body["publicKeyToShareWith"] as String?
        val shares = requestData.body["shares"] as Int
        val expiration = requestData.body["expiration"] as String?
        val emailToShareWith = requestData.body["emailToShareWith"] as String?

        val challenge = ChallengeHelper.findChallengeById(challengeId.toInt())

        val shareResult = DaoService.execute {
            ShareChallengeService.execute(user!!, challenge, shares, publicKeyToShareWith, emailToShareWith, expiration)
        }

        DaoService.throwOrReturn(shareResult)
        return shareResult.data!!
    }

    fun balances(user: UserAccount?, requestData: RequestData): SOAResult<EmailToChallengeBalanceList> {
        val challengeId = requestData.queryParams["challengeId"] as String

        val balancesResult = DaoService.execute {
            return@execute GetAllBalancesForChallengeService.execute(user!!, challengeId.toInt())
        }

        DaoService.throwOrReturn(balancesResult)
        return balancesResult.data!!
    }
}