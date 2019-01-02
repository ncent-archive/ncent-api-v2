package main.services.user

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.lang.Exception

/**
 * Update a users metadata
 * Make sure to verify email when using this service if the email is being updated!!
 */
object UpdateUserService: SOAServiceInterface<User> {
    override fun execute(caller: Int?, d: Any?, params: Map<String, String>?) : SOAResult<User> {
        val userNamespace = d!! as UserNamespace
        var userAccount = UserAccount.findById(caller!!)!!
        return try {
            transaction {
                userAccount.userMetadata.email = userNamespace.email
                userAccount.userMetadata.firstname = userNamespace.firstname
                userAccount.userMetadata.lastname = userNamespace.lastname
            }
            SOAResult(SOAResultType.SUCCESS, null, userAccount.userMetadata)
        } catch(e: Exception) {
            SOAResult(SOAResultType.FAILURE, e.message, null)
        }
    }
}