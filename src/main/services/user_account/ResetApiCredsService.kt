package main.services.user_account

import framework.models.idValue
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import main.services.transaction.GenerateTransactionService
import java.lang.Exception

/**
 * This service will be used to reset a user account's ApiCreds
 */
object ResetApiCredsService {
     fun execute(userAccount: UserAccount) : SOAResult<ApiCredNamespace> {
        val apiCredResult = GenerateApiCredsService.execute()
        if(apiCredResult.result != SOAResultType.SUCCESS)
            return SOAResult(apiCredResult.result, apiCredResult.message, null)
        val apiCredNamespace: ApiCredNamespace = apiCredResult.data!!
        val apiCreds = ApiCred.new {
            apiKey = apiCredNamespace.apiKey
            secretKey = apiCredNamespace.secretKey
        }

        return try {
            userAccount.apiCreds = apiCreds

            // TODO log or error result?
            val transactionResult = GenerateTransactionService.execute(
                    TransactionNamespace(
                            userAccount.cryptoKeyPair.publicKey,
                            null,
                            ActionNamespace(
                                    ActionType.UPDATE,
                                    userAccount.idValue,
                                    UserAccount::class.simpleName!!
                            ),
                            null, null
                    )
            )

            SOAResult(SOAResultType.SUCCESS, null, apiCredNamespace)
        } catch(e: Exception) {
            SOAResult(SOAResultType.FAILURE, e.message, null)
        }
    }
}
