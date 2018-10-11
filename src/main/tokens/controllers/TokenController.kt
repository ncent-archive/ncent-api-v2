package main.tokens.controllers


import kotlinserverless.framework.controllers.RestController
import kotlinserverless.framework.controllers.DefaultController
import kotlinserverless.framework.services.SOAResult
import main.tokens.models.Token
import kotlinserverless.framework.models.Request
import kotlinserverless.main.users.models.User

class TokenController: DefaultController<Token>(), RestController<Token, User> {
    override fun findOne(user: User, id: Int): SOAResult<Token> {
        throw NotImplementedError()
    }

    override fun create(user: User, element: Token): SOAResult<Token> {
        throw NotImplementedError()
    }

    fun transfer(user: User, request: Request): SOAResult<Token> {
        throw NotImplementedError()
    }
}