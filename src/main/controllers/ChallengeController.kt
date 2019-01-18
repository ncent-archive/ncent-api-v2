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

class ChallengeController: DefaultController<Challenge>(), RestController<Challenge, UserAccount> {
    override fun create(user: UserAccount, queryParams: Map<String, Any>): SOAResult<*> {
        validateApiKey(user, queryParams)
        return DaoService.execute {
            val challengeNamespace = JsonHelper.parse<ChallengeNamespace>(queryParams["challengeNamespace"]!!.toString())

            val generateChallengeResult = GenerateChallengeService.execute(user, challengeNamespace)
            DaoService.throwOrReturn(generateChallengeResult.result, generateChallengeResult.message)
            return@execute generateChallengeResult.data!!
        }
    }

    override fun findOne(user: UserAccount, queryParams: Map<String, Any>, id: Int): SOAResult<Challenge> {
        validateApiKey(user, queryParams)

        val challenge = ChallengeHelper.findChallengeById(id)

        return SOAResult(SOAResultType.SUCCESS, "", challenge)
    }

    override fun findAll(user: UserAccount, queryParams: Map<String, Any>): SOAResult<List<Challenge>> {
        validateApiKey(user, queryParams)

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
    fun share(user: UserAccount, request: Request): SOAResult<TransactionWithNewUser?> {
        val queryParams = validateApiKeyAndGetQueryParams(user, request)

        val finalResult = SOAResult<TransactionWithNewUser?>(SOAResultType.FAILURE, null, null)

        val challengeId = queryParams["challengeId"] as Int
        val publicKeyToShareWith = queryParams["publicKeyToShareWith"] as String?
        val shares = queryParams["shares"] as Int
        val expiration = queryParams["expiration"] as String?
        val emailToShareWith = queryParams["emailToShareWith"] as String?

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