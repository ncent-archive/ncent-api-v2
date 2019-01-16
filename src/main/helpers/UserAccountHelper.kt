package main.helpers

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import main.services.user_account.GenerateUserAccountService

object UserAccountHelper {
    fun getOrGenerateUser(
        email: String?,
        publicKey: String?
    ): SOAResult<Pair<String, NewUserAccount?>> {
        // validate users exist, if it does not generate one
        var newUserAccount: NewUserAccount? = null

        // get the public key
        val publicKey = when {
            /** if the email is provided make sure the user exists
             *   if the user does not exist we need to generate it
             *   if the email is not provided and the user does not exist we should error
             *   in order to share with a non-existant user we must have the email
             **/
            email != null -> {
                val userResult = User.find {
                    Users.email eq email
                }
                val userAccount = if(userResult.empty()) {
                    val newUserAccountResult = GenerateUserAccountService.execute(
                        uemail = email,
                        ufirstname = email.substringBefore("@"),
                        ulastname = ""
                    )

                    if(newUserAccountResult.result != SOAResultType.SUCCESS)
                        return SOAResult(newUserAccountResult.result, newUserAccountResult.message)
                    newUserAccount = newUserAccountResult.data
                    newUserAccount!!.value
                } else {
                    UserAccount.find {
                        UserAccounts.userMetadata eq userResult.first().id
                    }.first()
                }

                userAccount.cryptoKeyPair.publicKey
            }
            publicKey != null -> {
                val keyPair = CryptoKeyPair.find { CryptoKeyPairs.publicKey eq publicKey }
                if(keyPair.empty())
                    return SOAResult(SOAResultType.FAILURE, "The user does not exist. Must pass email in order to proceed.")
                publicKey
            }
            else ->
                return SOAResult(SOAResultType.FAILURE, "Must include an email or public key")
        }
        return SOAResult(SOAResultType.SUCCESS, null, Pair(publicKey, newUserAccount))
    }
}