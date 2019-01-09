package main.services.user_account

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.*
import framework.services.DaoService
import org.jetbrains.exposed.sql.select

object GetUserAccountService: SOAServiceInterface<UserAccount> {
    override fun execute(caller: Int?, id: Int?, params: Map<String, String>?): SOAResult<UserAccount> {
        val userAccountResult = DaoService.execute {
            when {
                id != null && id != 0 -> {
                    val query = UserAccounts
                        .select {
                            UserAccounts.id eq id!!
                        }.withDistinct()
                    UserAccount.wrapRows(query).toList().distinct().first()
                }
                params!!.containsKey("userId") -> {
                    UserAccount.findById((params["userId"] as String).toInt())!!
                }
                params.containsKey("email") -> {
                    val query = UserAccounts
                        .innerJoin(Users)
                        .select {
                            Users.email eq (params["email"] as String)
                        }
                    UserAccount.wrapRows(query).toList().distinct().first()
                }
                params.containsKey("apiKey") -> {
                    val query = UserAccounts
                        .innerJoin(ApiCreds)
                        .select {
                            ApiCreds.apiKey eq (params["apiKey"] as String)
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