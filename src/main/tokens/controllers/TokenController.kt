package main.tokens.controllers


import kotlinserverless.framework.controllers.RestController
import kotlinserverless.framework.controllers.DefaultController
import kotlinserverless.framework.models.ApiUser
import kotlinserverless.framework.services.SOAResult
import main.tokens.models.Token
import kotlinserverless.framework.models.Request

class TokenController: DefaultController<Token>(), RestController<Token, ApiUser> {
    override fun findOne(user: ApiUser, id: Int): SOAResult<Token> {
        throw NotImplementedError()
    }

    override fun create(user: ApiUser, element: Token): SOAResult<Token> {
        throw NotImplementedError()
    }

    fun transfer(user: ApiUser, request: Request): SOAResult<Token> {
        throw NotImplementedError()
    }
}