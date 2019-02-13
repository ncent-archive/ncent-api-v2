package main.services.user

import kotlinserverless.framework.models.Handler
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.lang.Exception

/**
 * Update a users metadata
 * Make sure to verify email when using this service if the email is being updated!!
 */
object UpdateUserService {
    fun execute(caller: UserAccount, userNamespace: UserNamespace) : SOAResult<User> {
        return try {
            transaction {
                caller.userMetadata.email = userNamespace.email
                caller.userMetadata.firstname = userNamespace.firstname
                caller.userMetadata.lastname = userNamespace.lastname
            }
            SOAResult(SOAResultType.SUCCESS, null, caller.userMetadata)
        } catch(e: Exception) {
            Handler.log(e, e.message)
            SOAResult(SOAResultType.FAILURE, e.message, null)
        }
    }
}