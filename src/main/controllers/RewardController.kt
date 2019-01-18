package main.controllers


import kotlinserverless.framework.controllers.RestController
import kotlinserverless.framework.controllers.DefaultController
import kotlinserverless.framework.models.Request
import kotlinserverless.framework.services.SOAResult
import main.daos.Reward
import main.daos.UserAccount

class RewardController: DefaultController<Reward>(), RestController<Reward, UserAccount> {
    override fun create(user: UserAccount, queryParams: Map<String, Any>): SOAResult<*> {
        throw NotImplementedError()
    }

    fun pool(user: UserAccount, request: Request): SOAResult<Reward> {
        throw NotImplementedError()
    }
}