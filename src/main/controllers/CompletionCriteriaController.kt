package main.controllers

import kotlinserverless.framework.controllers.RestController
import kotlinserverless.framework.controllers.DefaultController
import kotlinserverless.framework.services.SOAResult
import main.daos.CompletionCriteria
import main.daos.UserAccount

class CompletionCriteriaController: DefaultController<CompletionCriteria>(), RestController<CompletionCriteria, UserAccount> {
    override fun create(user: UserAccount, queryParams: Map<String, Any>): SOAResult<*> {
        throw NotImplementedError()
    }

    override fun update(user: UserAccount, queryParams: Map<String, Any>): SOAResult<CompletionCriteria> {
        throw NotImplementedError()
    }
}