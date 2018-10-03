package main.rewards.controllers


import kotlinserverless.framework.controllers.RestController
import kotlinserverless.framework.controllers.DefaultController
import kotlinserverless.framework.models.ApiUser
import kotlinserverless.framework.services.SOAResult
import main.rewards.models.Reward
import kotlinserverless.framework.models.Request

class RewardController: DefaultController<Reward>(), RestController<Reward, ApiUser> {
    override fun create(user: ApiUser, element: Reward): SOAResult<Reward> {
        throw NotImplementedError()
    }

    fun pool(user: ApiUser, request: Request): SOAResult<Reward> {
        throw NotImplementedError()
    }
}