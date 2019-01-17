package main.services.user_account

import framework.models.idValue
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import main.services.transaction.GenerateTransactionService
import java.lang.Exception

/**
 * This service is used to reset a user's public / private keypair.
 */
object ResetCryptoKeyPairService {
    fun execute(userAccount: UserAccount) : SOAResult<NewCryptoKeyPair> {
        val keypairResult = GenerateCryptoKeyPairService.execute()
        if(keypairResult.result != SOAResultType.SUCCESS)
            return SOAResult(keypairResult.result, keypairResult.message, null)

        return try {
            userAccount.cryptoKeyPair = keypairResult.data!!.value

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

            SOAResult(SOAResultType.SUCCESS, null, keypairResult.data!!)
        } catch(e: Exception) {
            SOAResult(SOAResultType.FAILURE, e.message, null)
        }
    }
}
