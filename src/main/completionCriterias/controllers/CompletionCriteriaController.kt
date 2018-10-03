package main.completionCriterias.controllers

import kotlinserverless.framework.controllers.RestController
import kotlinserverless.framework.controllers.DefaultController
import kotlinserverless.framework.models.ApiUser
import kotlinserverless.framework.services.SOAResult
import main.completionCriterias.models.CompletionCriteria

class CompletionCriteriaController: DefaultController<CompletionCriteria>(), RestController<CompletionCriteria, ApiUser> {
    override fun create(user: ApiUser, element: CompletionCriteria): SOAResult<CompletionCriteria> {
        throw NotImplementedError()
    }

    override fun update(user: ApiUser, element: CompletionCriteria): SOAResult<CompletionCriteria> {
        throw NotImplementedError()
    }
}