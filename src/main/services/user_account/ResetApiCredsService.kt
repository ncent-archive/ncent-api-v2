package main.services.user_account

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.lang.Exception

/**
 * This service will be used to reset a user account's ApiCreds
 */
object ResetApiCredsService: SOAServiceInterface<ApiCred> {
    override fun execute(caller: Int?) : SOAResult<ApiCred> {
        val apiCredResult = GenerateApiCredsService.execute()
        if(apiCredResult.result != SOAResultType.SUCCESS)
            return SOAResult(apiCredResult.result, apiCredResult.message, null)
        val apiCredNamespace: ApiCredNamespace = apiCredResult.data!!
        val apiCreds = ApiCred.new {
            apiKey = apiCredNamespace.apiKey
            secretKey = apiCredNamespace.secretKey
        }

        return try {
            val userAccount = UserAccount.findById(caller!!)!!
            transaction {
                userAccount.apiCreds= apiCreds
            }
            SOAResult(SOAResultType.SUCCESS, null, userAccount.apiCreds)
        } catch(e: Exception) {
            SOAResult(SOAResultType.FAILURE, e.message, null)
        }
    }
}
