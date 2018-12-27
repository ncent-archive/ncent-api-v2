package main.controllers

import kotlinserverless.framework.controllers.RestController
import kotlinserverless.framework.controllers.DefaultController
import kotlinserverless.framework.services.SOAResult
import main.daos.Challenge
import kotlinserverless.framework.models.Request
import main.daos.UserAccount

class ChallengeController: DefaultController<Challenge>(), RestController<Challenge, UserAccount> {
    override fun create(user: UserAccount, params: Map<String, String>): SOAResult<*> {
        throw NotImplementedError()
    }

    override fun findOne(user: UserAccount, id: Int): SOAResult<Challenge> {
        throw NotImplementedError()
    }

    fun expire(user: UserAccount, request: Request): SOAResult<Challenge> {
        throw NotImplementedError()
    }

    fun invalidate(user: UserAccount, request: Request): SOAResult<Challenge> {
        throw NotImplementedError()
    }

    fun validate(user: UserAccount, request: Request): SOAResult<Boolean> {
        throw NotImplementedError()
    }

    fun complete(user: UserAccount, request: Request): SOAResult<Challenge> {
        throw NotImplementedError()
    }

    fun share(user: UserAccount, request: Request): SOAResult<Challenge> {
        throw NotImplementedError()
    }
}