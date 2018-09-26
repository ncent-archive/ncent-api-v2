package main.userAccounts.services

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.userAccounts.models.ApiCreds

class GenerateApiCredsService: SOAServiceInterface<ApiCreds> {
    override fun execute(data: ApiCreds?, params: Map<String, String>?) : SOAResult<ApiCreds> {
        throw NotImplementedError()
    }
}