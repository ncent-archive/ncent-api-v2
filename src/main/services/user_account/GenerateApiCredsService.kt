package main.services.user_account

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.ApiCreds

/**
 * This service will be used to generate Api credentials
 */
class GenerateApiCredsService: SOAServiceInterface<ApiCreds> {
    override fun execute() : SOAResult<ApiCreds> {
        throw NotImplementedError()
    }
}