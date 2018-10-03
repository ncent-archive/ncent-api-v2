package main.challenges.controllers

import kotlinserverless.framework.controllers.RestController
import kotlinserverless.framework.controllers.DefaultController
import kotlinserverless.framework.models.ApiUser
import kotlinserverless.framework.services.SOAResult
import main.challenges.models.Challenge
import kotlinserverless.framework.models.Request

class ChallengeController: DefaultController<Challenge>(), RestController<Challenge, ApiUser> {
    override fun create(user: ApiUser, element: Challenge): SOAResult<Challenge> {
        throw NotImplementedError()
    }

    override fun findOne(user: ApiUser, id: Int): SOAResult<Challenge> {
        throw NotImplementedError()
    }

    fun expire(user: ApiUser, request: Request): SOAResult<Challenge> {
        throw NotImplementedError()
    }

    fun invalidate(user: ApiUser, request: Request): SOAResult<Challenge> {
        throw NotImplementedError()
    }

    fun validate(user: ApiUser, request: Request): SOAResult<Boolean> {
        throw NotImplementedError()
    }

    fun complete(user: ApiUser, request: Request): SOAResult<Challenge> {
        throw NotImplementedError()
    }

    fun share(user: ApiUser, request: Request): SOAResult<Challenge> {
        throw NotImplementedError()
    }
}