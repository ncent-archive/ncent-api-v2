package main.controllers


import kotlinserverless.framework.controllers.RestController
import kotlinserverless.framework.controllers.DefaultController
import kotlinserverless.framework.services.SOAResult
import main.daos.Token
import kotlinserverless.framework.models.Request
import main.daos.User
import main.daos.UserAccount

class TokenController: DefaultController<Token>(), RestController<Token, UserAccount> {
    override fun findOne(user: UserAccount, id: Int): SOAResult<Token> {
        throw NotImplementedError()
    }

    override fun create(user: UserAccount, params: Map<String, String>): SOAResult<*> {
        throw NotImplementedError()
    }

    fun transfer(user: UserAccount, request: Request): SOAResult<Token> {
        throw NotImplementedError()
    }
}