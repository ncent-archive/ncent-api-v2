package main.controllers


import kotlinserverless.framework.controllers.RestController
import kotlinserverless.framework.controllers.DefaultController
import kotlinserverless.framework.services.SOAResult
import main.daos.Reward
import main.daos.UserAccount

class RewardController: DefaultController<Reward>(), RestController<Reward, UserAccount> {
    override fun create(user: UserAccount, params: Map<String, String>): SOAResult<*> {
        throw NotImplementedError()
    }

    fun pool(user: UserAccount, request: Any): SOAResult<Reward> {
        throw NotImplementedError()
    }
}