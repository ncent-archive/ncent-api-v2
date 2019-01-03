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
            val query = UserAccounts
                .select {
                    UserAccounts.id eq id!!
                }.withDistinct()
            UserAccount.wrapRows(query).toList().distinct()
        }

        if (userAccountResult.result != SOAResultType.SUCCESS)
            return SOAResult(userAccountResult.result, userAccountResult.message, null)
        return SOAResult(SOAResultType.SUCCESS, null, userAccountResult.data!!.first())
    }
}
