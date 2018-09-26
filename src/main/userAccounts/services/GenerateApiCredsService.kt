package main.userAccounts.services

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.userAccounts.models.ApiCreds

/**
 * This service will be used to generate Api credentials
 */
class GenerateApiCredsService: SOAServiceInterface<ApiCreds> {
    override fun execute(caller: Int?) : SOAResult<ApiCreds> {
        throw NotImplementedError()
    }
}