package main.controllers

import kotlinserverless.framework.controllers.RestController
import kotlinserverless.framework.controllers.DefaultController
import kotlinserverless.framework.services.SOAResult
import main.daos.CompletionCriteria
import main.daos.UserAccount

class CompletionCriteriaController: DefaultController<CompletionCriteria>(), RestController<CompletionCriteria, UserAccount> {
    override fun create(user: UserAccount, params: Map<String, String>): SOAResult<*> {
        throw NotImplementedError()
    }

    override fun update(user: UserAccount, params: Map<String, String>): SOAResult<CompletionCriteria> {
        throw NotImplementedError()
    }
}