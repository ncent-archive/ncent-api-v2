package main.challenges.controllers

import kotlinserverless.framework.controllers.RestController
import kotlinserverless.framework.controllers.DefaultController
import kotlinserverless.framework.services.SOAResult
import main.challenges.models.Challenge
import kotlinserverless.framework.models.Request
import kotlinserverless.main.users.models.User

class ChallengeController: DefaultController<Challenge>(), RestController<Challenge, User> {
    override fun create(user: User, element: Challenge): SOAResult<Challenge> {
        throw NotImplementedError()
    }

    override fun findOne(user: User, id: Int): SOAResult<Challenge> {
        throw NotImplementedError()
    }

    fun expire(user: User, request: Request): SOAResult<Challenge> {
        throw NotImplementedError()
    }

    fun invalidate(user: User, request: Request): SOAResult<Challenge> {
        throw NotImplementedError()
    }

    fun validate(user: User, request: Request): SOAResult<Boolean> {
        throw NotImplementedError()
    }

    fun complete(user: User, request: Request): SOAResult<Challenge> {
        throw NotImplementedError()
    }

    fun share(user: User, request: Request): SOAResult<Challenge> {
        throw NotImplementedError()
    }
}