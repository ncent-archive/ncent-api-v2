package kotlinserverless.framework.services

import kotlinserverless.framework.healthchecks.InvalidEndpoint

interface SOAServiceInterface<T> {
    fun execute() : SOAResult<T> {
        throw InvalidEndpoint()
    }

    // GET/UPDATE a single result by id and filters
    fun execute(id: Int?, params: Map<String, String>?) : SOAResult<T> {
        throw InvalidEndpoint()
    }

    // GET/UPDATE a single result by key and filters
    fun execute(key: String?, params: Map<String, String>?) : SOAResult<T> {
        throw InvalidEndpoint()
    }

    // GET/UPDATE multiple results by filters
    fun execute(params: Map<String, String>?) : SOAResult<List<T>> {
        throw InvalidEndpoint()
    }

    // CREATE/UPDATE a single object
    fun execute(data: T?, params: Map<String, String>?) : SOAResult<T> {
        throw InvalidEndpoint()
    }

    // CREATE/UPDATE multiple objects
    fun execute(dataList: List<T>?, params: Map<String, String>?) : SOAResult<List<T>> {
        throw InvalidEndpoint()
    }
}