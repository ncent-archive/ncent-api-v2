package main.controllers


import kotlinserverless.framework.controllers.RestController
import kotlinserverless.framework.controllers.DefaultController
import kotlinserverless.framework.models.Request
import kotlinserverless.framework.services.SOAResult
import main.daos.Token
import main.daos.UserAccount

class TokenController: DefaultController<Token>(), RestController<Token, UserAccount> {
    override fun findOne(user: UserAccount, queryParams: Map<String, Any>, id: Int): SOAResult<Token> {
        throw NotImplementedError()
    }

    override fun create(user: UserAccount, queryParams: Map<String, Any>): SOAResult<*> {
        throw NotImplementedError()
    }

    fun transfer(user: UserAccount, request: Request): SOAResult<Token> {
        throw NotImplementedError()
    }
}