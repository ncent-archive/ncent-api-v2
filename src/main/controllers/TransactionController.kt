package main.controllers

import kotlinserverless.framework.controllers.RestController
import kotlinserverless.framework.controllers.DefaultController
import kotlinserverless.framework.services.SOAResult
import main.daos.Transaction
import main.daos.UserAccount

class TransactionController: DefaultController<Transaction>(), RestController<Transaction, UserAccount> {
    override fun create(user: UserAccount, params: Map<String, String>): SOAResult<*> {
        throw NotImplementedError()
    }
}