package main.controllers

import kotlinserverless.framework.controllers.RestController
import kotlinserverless.framework.controllers.DefaultController
import kotlinserverless.framework.services.SOAResult
import main.daos.Transaction
import main.daos.UserAccount
import main.helpers.ControllerHelper.RequestData

class TransactionController: DefaultController<Transaction>(), RestController<Transaction, UserAccount> {
    override fun create(user: UserAccount, requestData: RequestData): SOAResult<*> {
        throw NotImplementedError()
    }
}