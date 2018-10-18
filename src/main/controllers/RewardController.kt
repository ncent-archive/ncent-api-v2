package main.controllers


import kotlinserverless.framework.controllers.RestController
import kotlinserverless.framework.controllers.DefaultController
import kotlinserverless.framework.services.SOAResult
import main.daos.Reward
import kotlinserverless.framework.models.Request
import main.daos.User

class RewardController: DefaultController<Reward>(), RestController<Reward, User> {
    override fun create(user: User, element: Reward): SOAResult<Reward> {
        throw NotImplementedError()
    }

    fun pool(user: User, request: Request): SOAResult<Reward> {
        throw NotImplementedError()
    }
}