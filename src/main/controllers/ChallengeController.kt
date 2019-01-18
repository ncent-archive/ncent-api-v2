package main.controllers

import framework.services.DaoService
import kotlinserverless.framework.controllers.RestController
import kotlinserverless.framework.controllers.DefaultController
import kotlinserverless.framework.models.ForbiddenException
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.models.Request
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import main.services.challenge.*
import main.helpers.JsonHelper
import main.helpers.ChallengeHelper
import main.helpers.ControllerHelper.RequestData

class ChallengeController: DefaultController<Challenge>(), RestController<Challenge, UserAccount> {
    override fun create(user: UserAccount?, requestData: RequestData): SOAResult<Challenge> {
        validateApiKey(user!!, requestData)
        return DaoService.execute {
            val challengeNamespace = JsonHelper.parse<ChallengeNamespace>(requestData.body["challengeNamespace"]!!.toString())

            val generateChallengeResult = GenerateChallengeService.execute(user, challengeNamespace)
            DaoService.throwOrReturn(generateChallengeResult.result, generateChallengeResult.message)
            return@execute generateChallengeResult.data!!
        }
    }

    override fun findOne(user: UserAccount, requestData: RequestData, id: Int): SOAResult<Challenge> {
        validateApiKey(user, requestData)

        val challenge = ChallengeHelper.findChallengeById(id)

        return SOAResult(SOAResultType.SUCCESS, "", challenge)
    }

    override fun findAll(user: UserAccount, requestData: RequestData): SOAResult<List<Challenge>> {
        validateApiKey(user, requestData)

        val challengesResult = ChallengeHelper.getChallenges(user)

        if (challengesResult.data == null) {
            throw InternalError()
        }

        return SOAResult(SOAResultType.SUCCESS, challengesResult.message, challengesResult.data!!.challengeToUnsharedTransactions.map { it -> it.first})
    }

    @Throws(ForbiddenException::class)
    fun complete(user: UserAccount, request: Request): SOAResult<TransactionList> {
        val completerPublicKey = request.input["completerPublicKey"] as String
        val challengeId = request.input["challengeId"] as Int
        val challenge = ChallengeHelper.findChallengeById(challengeId)

        val finalResult = DaoService.execute {
            CompleteChallengeService.execute(user, challenge, completerPublicKey)
        }.data

        if (finalResult?.result == SOAResultType.FAILURE &&
                (finalResult.message?.contains("Cannot transition from") == true || finalResult.message?.contains("This user cannot change the challenge state") == true)) {
            throw ForbiddenException(finalResult.message!!)
        }

        return finalResult!!
    }

    @Throws(ForbiddenException::class)
    fun redeem(user: UserAccount, request: Request): SOAResult<TransactionList> {
        val completerPublicKey = request.input["completerPublicKey"] as String
        val challengeId = request.input["challengeId"] as Int
        val challenge = ChallengeHelper.findChallengeById(challengeId)

        val finalResult = DaoService.execute {
            RedeemChallengeService.execute(user, challenge, completerPublicKey)
        }.data

        if (finalResult?.result == SOAResultType.FAILURE &&
                (finalResult.message?.contains("Cannot transition from") == true || finalResult.message?.contains("Challenge has not been activated") == true)) {
            throw ForbiddenException(finalResult.message!!)
        }

        return finalResult!!
    }

    @Throws(ForbiddenException::class)
    fun share(user: UserAccount, requestData: RequestData): SOAResult<TransactionWithNewUser?> {
        validateApiKey(user, requestData)

        val finalResult = SOAResult<TransactionWithNewUser?>(SOAResultType.FAILURE, null, null)

        val challengeId = requestData.body["challengeId"] as Int
        val publicKeyToShareWith = requestData.body["publicKeyToShareWith"] as String?
        val shares = requestData.body["shares"] as Int
        val expiration = requestData.body["expiration"] as String?
        val emailToShareWith = requestData.body["emailToShareWith"] as String?

        val challenge = ChallengeHelper.findChallengeById(challengeId)

        DaoService.execute {
            val serviceResult = ShareChallengeService.execute(user, challenge, shares, publicKeyToShareWith, emailToShareWith, expiration)
            finalResult.result = serviceResult.result
            finalResult.message = serviceResult.message
            finalResult.data = serviceResult.data
        }

        if (finalResult.result != SOAResultType.SUCCESS && finalResult.message?.contains("You do not have enough valid shares available") == true) {
            throw ForbiddenException(finalResult.message as String)
        }

        return finalResult
    }
}