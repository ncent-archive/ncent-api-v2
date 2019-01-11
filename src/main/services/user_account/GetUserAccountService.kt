package main.services.user_account

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import framework.services.DaoService
import org.jetbrains.exposed.sql.select

object GetUserAccountService {
    fun execute(userId: Int? = null, email: String?, apiKey: String?): SOAResult<UserAccount> {
        val userAccountResult = DaoService.execute {
            when {
                userId != null -> {
                    UserAccount.findById(userId)!!
                }
                email != null -> {
                    val query = UserAccounts
                        .innerJoin(Users)
                        .select {
                            Users.email eq email
                        }
                    UserAccount.wrapRows(query).toList().distinct().first()
                }
                apiKey != null -> {
                    val query = UserAccounts
                        .innerJoin(ApiCreds)
                        .select {
                            ApiCreds.apiKey eq apiKey
                        }
                    UserAccount.wrapRows(query).toList().distinct().first()
                }
                else -> {
                    throw Exception("Could not find user without email/apiKey/userId")
                }
            }
        }

        if (userAccountResult.result != SOAResultType.SUCCESS)
            return SOAResult(userAccountResult.result, userAccountResult.message, null)
        return SOAResult(SOAResultType.SUCCESS, null, userAccountResult.data!!)
    }
}