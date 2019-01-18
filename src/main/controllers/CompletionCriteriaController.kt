package main.controllers

import kotlinserverless.framework.controllers.RestController
import kotlinserverless.framework.controllers.DefaultController
import kotlinserverless.framework.services.SOAResult
import main.daos.CompletionCriteria
import main.daos.UserAccount
import main.helpers.ControllerHelper.RequestData

class CompletionCriteriaController: DefaultController<CompletionCriteria>(), RestController<CompletionCriteria, UserAccount> {
    override fun create(user: UserAccount, requestData: RequestData): SOAResult<*> {
        throw NotImplementedError()
    }

    override fun update(user: UserAccount, requestData: RequestData): SOAResult<CompletionCriteria> {
        throw NotImplementedError()
    }
}