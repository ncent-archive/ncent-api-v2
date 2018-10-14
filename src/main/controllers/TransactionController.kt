package main.controllers

import kotlinserverless.framework.controllers.RestController
import kotlinserverless.framework.controllers.DefaultController
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.main.daos.User
import main.daos.Transaction

class TransactionController: DefaultController<Transaction>(), RestController<Transaction, User> {
    override fun create(user: User, element: Transaction): SOAResult<Transaction> {
        throw NotImplementedError()
    }
}