package main.completionCriterias.controllers

import kotlinserverless.framework.controllers.RestController
import kotlinserverless.framework.controllers.DefaultController
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.main.users.models.User
import main.completionCriterias.models.CompletionCriteria

class CompletionCriteriaController: DefaultController<CompletionCriteria>(), RestController<CompletionCriteria, User> {
    override fun create(user: User, element: CompletionCriteria): SOAResult<CompletionCriteria> {
        throw NotImplementedError()
    }

    override fun update(user: User, element: CompletionCriteria): SOAResult<CompletionCriteria> {
        throw NotImplementedError()
    }
}