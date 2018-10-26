package main.controllers

import kotlinserverless.framework.controllers.RestController
import kotlinserverless.framework.controllers.DefaultController
import kotlinserverless.framework.services.SOAResult
import main.daos.User
import main.daos.CompletionCriteria

class CompletionCriteriaController: DefaultController<CompletionCriteria>(), RestController<CompletionCriteria, User> {
    override fun create(user: User, element: CompletionCriteria): SOAResult<CompletionCriteria> {
        throw NotImplementedError()
    }

    override fun update(user: User, element: CompletionCriteria): SOAResult<CompletionCriteria> {
        throw NotImplementedError()
    }
}