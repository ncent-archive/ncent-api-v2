package main.controllers

import framework.services.DaoService
import kotlinserverless.framework.controllers.RestController
import kotlinserverless.framework.controllers.DefaultController
import kotlinserverless.framework.models.ForbiddenException
import kotlinserverless.framework.models.NotFoundException
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.models.Request
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import main.services.user_account.ValidateApiKeyService
import main.services.challenge.*
import main.helpers.JsonHelper

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

class ChallengeController: DefaultController<Challenge>(), RestController<Challenge, UserAccount> {
    override fun create(user: UserAccount, params: Map<String, String>): SOAResult<*> {
        return DaoService.execute {
            ValidateApiKeyService.execute(user, params["secretKey"] as String)
            val challengeNamespace = JsonHelper.parse<ChallengeNamespace>(params["challengeNamespace"]!!)

            val generateChallengeResult = GenerateChallengeService.execute(user, challengeNamespace)
            DaoService.throwOrReturn(generateChallengeResult.result, generateChallengeResult.message)
            return@execute generateChallengeResult.data!!
        }
    }

    override fun findOne(user: UserAccount, id: Int): SOAResult<Challenge> {
        val challenge = findChallengeById(id)

        return SOAResult(SOAResultType.SUCCESS, "", challenge)
    }

    fun complete(user: UserAccount, request: Request): SOAResult<Challenge> {
        throw NotImplementedError()
    }

    @Throws(ForbiddenException::class)
    fun share(user: UserAccount, request: Request): SOAResult<TransactionWithNewUser?> {
        val finalResult = SOAResult<TransactionWithNewUser?>(SOAResultType.FAILURE, null, null)

        val challengeId = request.input["challengeId"] as Int
        val publicKeyToShareWith = request.input["publicKeyToShareWith"] as String?
        val shares = request.input["shares"] as Int
        val expiration = request.input["expiration"] as String?
        val emailToShareWith = request.input["emailToShareWith"] as String?

        val validateApiKeyResult = DaoService.execute {
            ValidateApiKeyService.execute(user, request.input["secretKey"] as String)
        }
        DaoService.throwOrReturn(validateApiKeyResult.result, validateApiKeyResult.message)

        val challenge = findChallengeById(challengeId)

        DaoService.execute {
            val serviceResult = ShareChallengeService.execute(user, challenge, shares, publicKeyToShareWith, emailToShareWith, expiration)
            finalResult.result = serviceResult.result
            finalResult.message = serviceResult.message
            finalResult.data = serviceResult.data
        }

        if (finalResult.result != SOAResultType.SUCCESS && finalResult.message != null) {
            throw ForbiddenException(finalResult.message as String)
        }

        return finalResult
    }
}