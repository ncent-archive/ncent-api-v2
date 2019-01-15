package main.helpers

import framework.models.idValue
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import main.services.user_account.GenerateUserAccountService

object UserAccountHelper {
    fun getOrGenerateUserToShareWith(
        emailToShareWith: String?,
        publicKeyToShareWith: String?
    ): SOAResult<Pair<String, NewUserAccount?>> {
        // validate users exist, if it does not generate one
        var newUserAccount: NewUserAccount? = null

        // get the public key we need to share with
        val publicKeyToShareWith = when {
            /** if the email is provided make sure the user exists
             *   if the user does not exist we need to generate it
             *   if the email is not provided and the user does not exist we should error
             *   in order to share with a non-existant user we must have the email
             **/
            emailToShareWith != null -> {
                val userResult = User.find {
                    Users.email eq emailToShareWith
                }
                val userAccount = if(userResult.empty()) {
                    val newUserAccountResult = GenerateUserAccountService.execute(
                        uemail = emailToShareWith,
                        ufirstname = emailToShareWith.substringBefore("@"),
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
            publicKeyToShareWith != null -> {
                val keyPair = CryptoKeyPair.find { CryptoKeyPairs.publicKey eq publicKeyToShareWith }
                if(keyPair.empty())
                    return SOAResult(SOAResultType.FAILURE, "The user does not exist. In order to share with that user you must share via email.")
                publicKeyToShareWith
            }
            else ->
                return SOAResult(SOAResultType.FAILURE, "Must include an email or public key to share with")
        }
        return SOAResult(SOAResultType.SUCCESS, null, Pair(publicKeyToShareWith, newUserAccount))
    }
}