package main.transactions.controllers

import kotlinserverless.framework.controllers.RestController
import kotlinserverless.framework.controllers.DefaultController
import kotlinserverless.framework.models.ApiUser
import kotlinserverless.framework.services.SOAResult
import main.transactions.models.Transaction

class TransactionController: DefaultController<Transaction<Any>>(), RestController<Transaction<Any>, ApiUser> {
    override fun create(user: ApiUser, element: Transaction<Any>): SOAResult<Transaction<Any>> {
        throw NotImplementedError()
    }
}