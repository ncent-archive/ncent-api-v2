package main.helpers

import kotlinserverless.framework.models.InvalidArguments
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import main.services.user_account.GenerateUserAccountService
import org.jetbrains.exposed.sql.select
import org.glassfish.jersey.internal.util.Base64
import main.helpers.ControllerHelper.UserAuth

object UserAccountHelper {
    fun getUserAuth(user: NewUserAccount): String {
        val apiKey = user.value.apiCreds.apiKey
        val encodedApiKeyAndSecret = Base64.encodeAsString("$apiKey:${user.secretKey}")
        return "Basic $encodedApiKeyAndSecret"
    }

    fun getUserAuth(headers: Map<String, Any>): UserAuth? {
        if(!headers.containsKey("Authorization"))
            return null
        val authHeaderSplit = (headers["Authorization"] as String).split(" ")
        if(authHeaderSplit.size != 2 && authHeaderSplit[0] != "Basic")
            throw InvalidArguments("This is not a properly formatted user auth header. Must include 'Basic apikey:secretkey'")
        val base64DecodedAuth = Base64.decodeAsString(authHeaderSplit[1])
        val keyAndSecret = base64DecodedAuth.split(":".toRegex(), 2)
        if(keyAndSecret.size != 2)
            throw InvalidArguments("The user authentication parameters are not formatted correctly. Should be apikey:secret")
        return UserAuth(keyAndSecret[0], keyAndSecret[1])
    }

    fun getOrGenerateUser(
        email: String?,
        publicKey: String?
    ): SOAResult<Pair<UserAccount, NewUserAccount?>> {
        // validate users exist, if it does not generate one
        var newUserAccount: NewUserAccount? = null

        // get the user account
        val userAccount = when {
            /** if the email is provided make sure the user exists
             *   if the user does not exist we need to generate it
             *   if the email is not provided and the user does not exist we should error
             *   in order to share with a non-existant user we must have the email
             **/
            email != null -> {
                val query = UserAccounts
                    .innerJoin(Users)
                    .select {
                        Users.email eq email
                    }.withDistinct()
                val userAccounts = UserAccount.wrapRows(query).toList().distinct()

                if(userAccounts.isEmpty()) {
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
                    userAccounts.first()
                }
            }
            publicKey != null -> {
                val query = UserAccounts
                    .innerJoin(CryptoKeyPairs)
                    .select {
                        CryptoKeyPairs.publicKey eq publicKey
                    }.withDistinct()
                val userAccounts = UserAccount.wrapRows(query).toList().distinct()

                if(userAccounts.isEmpty())
                    return SOAResult(SOAResultType.FAILURE, "The user does not exist. Must pass email in order to proceed.")
                userAccounts.first()
            }
            else ->
                return SOAResult(SOAResultType.FAILURE, "Must include an email or public key")
        }
        return SOAResult(SOAResultType.SUCCESS, null, Pair(userAccount, newUserAccount))
    }
}