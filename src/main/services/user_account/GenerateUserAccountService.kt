package main.services.user_account

import framework.models.idValue
import framework.services.DaoService
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import main.services.transaction.GenerateTransactionService

/**
 * This service will be used to generate a full User Account
 */
object GenerateUserAccountService {
    fun execute(uemail: String, ufirstname: String, ulastname: String) : SOAResult<NewUserAccount> {
        val apiCredResult = GenerateApiCredsService.execute()
        if(apiCredResult.result != SOAResultType.SUCCESS)
            return SOAResult(apiCredResult.result, apiCredResult.message, null)
        val apiCredNamespace: ApiCredNamespace = apiCredResult.data!!

        val sessionResult = StartSessionService.execute()
        if(sessionResult.result != SOAResultType.SUCCESS)
            return SOAResult(apiCredResult.result, sessionResult.message, null)
        val sessionNamespace: SessionNamespace = sessionResult.data!!

        return DaoService.execute {
            val user = User.new {
                email = uemail
                firstname = ufirstname
                lastname = ulastname
            }
            val apiCred = ApiCred.new {
                apiKey = apiCredNamespace.apiKey
                secretKey = apiCredNamespace.secretKey
            }
            val newSession = Session.new {
                sessionKey = sessionNamespace.sessionKey
                expiration = sessionNamespace.expiration
            }

            val keyPairResult = GenerateCryptoKeyPairService.execute()
            if(keyPairResult.result != SOAResultType.SUCCESS)
                throw Exception(keyPairResult.message)

            val keyPairData = keyPairResult.data!!
            val userAccount = UserAccount.new {
                userMetadata = user
                cryptoKeyPair = keyPairData.value
                apiCreds = apiCred
                session = newSession
            }

            // TODO log or error result?
            val transactionResult = GenerateTransactionService.execute(
                TransactionNamespace(
                    keyPairData.value.publicKey,
                    null,
                    ActionNamespace(
                        ActionType.CREATE,
                        userAccount.idValue,
                        UserAccount::class.simpleName!!
                    ),
                    null, null
                )
            )

            return@execute NewUserAccount(
                userAccount,
                keyPairData.secret,
                apiCredNamespace.secretKey
            )
        }
    }
}
