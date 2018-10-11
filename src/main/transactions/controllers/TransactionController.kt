package main.transactions.controllers

import kotlinserverless.framework.controllers.RestController
import kotlinserverless.framework.controllers.DefaultController
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.main.users.models.User
import main.transactions.models.Transaction

class TransactionController: DefaultController<Transaction<Any>>(), RestController<Transaction<Any>, User> {
    override fun create(user: User, element: Transaction<Any>): SOAResult<Transaction<Any>> {
        throw NotImplementedError()
    }
}