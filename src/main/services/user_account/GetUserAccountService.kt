package main.services.user_account

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import framework.services.DaoService
import kotlinserverless.framework.models.NotFoundException
import org.jetbrains.exposed.sql.select
import main.services.user_account.ValidateJWTService
import com.auth0.jwt.JWT

object GetUserAccountService {
    fun execute(userId: Int? = null, email: String? = null, apiKey: String? = null, jwt: String? = null): SOAResult<UserAccount> {
        return DaoService.execute {
            try {
                when {
                    jwt != null -> {
                        println("here1");
                        val validation = ValidateJWTService.execute(jwt)
                        println("here2");
                        if(validation.result != SOAResultType.SUCCESS) {
                            throw NotFoundException()
                        } else {
                            val email = JWT.decode(jwt).getClaim("email").asString()
                            println("here email")
                            val query = UserAccounts
                                .innerJoin(Users)
                                .select {
                                    Users.email eq email
                                }
                            UserAccount.wrapRows(query).toList().distinct().first()
                        }
                    }
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