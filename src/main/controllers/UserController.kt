package main.controllers

import kotlinserverless.framework.controllers.RestController
import kotlinserverless.framework.controllers.DefaultController
import kotlinserverless.framework.services.SOAResult
import main.daos.User
import kotlinserverless.main.services.users.UserHelloService
import main.daos.UserAccount
import main.helpers.ControllerHelper.RequestData

class UserController: DefaultController<User>(), RestController<User, UserAccount> {
    fun hello(user: UserAccount?, requestData: RequestData): SOAResult<String> {
        return UserHelloService.execute()
    }
}