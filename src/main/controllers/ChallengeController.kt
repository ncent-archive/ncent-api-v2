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
        return DaoService.execute {
            val challengeNamespace = JsonHelper.parse<ChallengeNamespace>(requestData.body["challengeNamespace"]!! as JsonObject)

            val generateChallengeResult = GenerateChallengeService.execute(user!!, challengeNamespace)
            DaoService.throwOrReturn(generateChallengeResult)
            return@execute generateChallengeResult.data!!
        }
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

        val daoResult = DaoService.execute {
            CompleteChallengeService.execute(user!!, challenge, completerPublicKey)
        }

        DaoService.throwOrReturn(daoResult)

        val completeResult = daoResult.data!!

        if (completeResult.result == SOAResultType.FAILURE &&
                (completeResult.message?.contains("Cannot transition from") == true || completeResult.message?.contains("This user cannot change the challenge state") == true)) {
            throw ForbiddenException(completeResult.message!!)
        }

        return completeResult
    }

    @Throws(ForbiddenException::class)
    fun redeem(user: UserAccount?, requestData: RequestData): SOAResult<TransactionList> {
        val completerPublicKey = requestData.body["completerPublicKey"] as String
        val challengeId = requestData.body["challengeId"] as Int
        val challenge = ChallengeHelper.findChallengeById(challengeId)

        val daoResult = DaoService.execute {
            RedeemChallengeService.execute(user!!, challenge, completerPublicKey)
        }

        DaoService.throwOrReturn(daoResult)

        val redeemResult = daoResult.data!!

        if (redeemResult.result == SOAResultType.FAILURE &&
                (redeemResult.message?.contains("Cannot transition from") == true || redeemResult.message?.contains("Challenge has not been activated") == true)) {
            throw ForbiddenException(redeemResult.message!!)
        }

        return redeemResult
    }

    @Throws(ForbiddenException::class)
    fun share(user: UserAccount?, requestData: RequestData): SOAResult<TransactionWithNewUser> {
        val challengeId = requestData.body["challengeId"] as String
        val publicKeyToShareWith = requestData.body["publicKeyToShareWith"] as String?
        val shares = requestData.body["shares"] as Int
        val expiration = requestData.body["expiration"] as String?
        val emailToShareWith = requestData.body["emailToShareWith"] as String?

        val challenge = ChallengeHelper.findChallengeById(challengeId.toInt())

        val daoResult = DaoService.execute {
            ShareChallengeService.execute(user!!, challenge, shares, publicKeyToShareWith, emailToShareWith, expiration)
        }

        DaoService.throwOrReturn(daoResult)

        val shareResult = daoResult.data!!

        if (shareResult.result != SOAResultType.SUCCESS && shareResult.message?.contains("You do not have enough valid shares available") == true) {
            throw ForbiddenException(shareResult.message as String)
        }

        return shareResult
    }

    fun balances(user: UserAccount?, requestData: RequestData): SOAResult<EmailToChallengeBalanceList> {
        val challengeId = requestData.queryParams["challengeId"] as String

        val daoResult = DaoService.execute {
            GetAllBalancesForChallengeService.execute(user!!, challengeId.toInt())
        }

        DaoService.throwOrReturn(daoResult)

        val challengeBalancesResult = daoResult.data!!

        if (challengeBalancesResult.data == null && challengeBalancesResult.message == "User not permitted to make this call") {
            throw ForbiddenException(challengeBalancesResult.message)
        }

        return challengeBalancesResult
    }
}