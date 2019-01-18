package main.controllers


import kotlinserverless.framework.controllers.RestController
import kotlinserverless.framework.controllers.DefaultController
import kotlinserverless.framework.services.SOAResult
import main.daos.Token
import main.daos.UserAccount
import main.helpers.ControllerHelper.RequestData

class TokenController: DefaultController<Token>(), RestController<Token, UserAccount> {
    override fun findOne(user: UserAccount, requestData: RequestData, id: Int): SOAResult<Token> {
        throw NotImplementedError()
    }

    override fun create(user: UserAccount?, requestData: RequestData): SOAResult<*> {
        throw NotImplementedError()
    }

    fun transfer(user: UserAccount, requestData: RequestData): SOAResult<Token> {
        throw NotImplementedError()
    }
}