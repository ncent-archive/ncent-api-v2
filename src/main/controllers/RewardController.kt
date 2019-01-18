package main.controllers


import kotlinserverless.framework.controllers.RestController
import kotlinserverless.framework.controllers.DefaultController
import kotlinserverless.framework.services.SOAResult
import main.daos.Reward
import main.daos.UserAccount
import main.helpers.ControllerHelper.RequestData

class RewardController: DefaultController<Reward>(), RestController<Reward, UserAccount> {
    override fun create(user: UserAccount?, requestData: RequestData): SOAResult<*> {
        throw NotImplementedError()
    }

    fun pool(user: UserAccount, requestData: RequestData): SOAResult<Reward> {
        throw NotImplementedError()
    }
}