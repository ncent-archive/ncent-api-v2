package main.services.user_account

import kotlinserverless.framework.services.SOAResult
import main.daos.*
import framework.services.DaoService
import kotlinserverless.framework.models.NotFoundException
import org.jetbrains.exposed.sql.select

object GetUserAccountService {
    fun execute(userId: Int? = null, email: String? = null, apiKey: String? = null): SOAResult<UserAccount> {
        return DaoService.execute {
            try {
                when {
                    apiKey != null -> {
                        val query = UserAccounts
                                .innerJoin(ApiCreds)
                                .select {
                                    ApiCreds.apiKey eq apiKey
                                }
                        UserAccount.wrapRows(query).toList().distinct().first()
                    }
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
                    else -> {
                        throw NotFoundException()
                    }
                }
            } catch(e: NoSuchElementException) {
                throw NotFoundException()
            }
        }
    }
}