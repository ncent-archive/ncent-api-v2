package kotlinserverless.framework.services

import kotlinserverless.framework.healthchecks.InvalidEndpoint

interface SOAServiceInterface<T> {
    fun execute(data: T?, params: Map<String, String>?) : SOAResult<T> {
        throw InvalidEndpoint()
    }

    fun execute(dataList: List<T>?, params: Map<String, String>?) : SOAResult<T> {
        throw InvalidEndpoint()
    }
}