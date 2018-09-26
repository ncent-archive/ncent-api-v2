package kotlinserverless.framework.services

import kotlinserverless.framework.healthchecks.InvalidEndpoint
import kotlinserverless.framework.models.Model

interface SOAServiceInterface<T> {
    fun execute(caller: Model) : SOAResult<T> {
        throw InvalidEndpoint()
    }

    // GET/UPDATE a single result by id and filters
    fun execute(caller: Model, id: Int?, params: Map<String, String>?) : SOAResult<T> {
        throw InvalidEndpoint()
    }

    // GET/UPDATE a single result by key and filters
    fun execute(caller: Model, key: String?, params: Map<String, String>?) : SOAResult<T> {
        throw InvalidEndpoint()
    }

    // GET/UPDATE multiple results by filters
    fun execute(caller: Model, params: Map<String, String>?) : SOAResult<List<T>> {
        throw InvalidEndpoint()
    }

    // CREATE/UPDATE a single object
    fun execute(caller: Model, data: T?, params: Map<String, String>?) : SOAResult<T> {
        throw InvalidEndpoint()
    }

    // CREATE/UPDATE multiple objects
    fun execute(caller: Model, dataList: List<T>?, params: Map<String, String>?) : SOAResult<List<T>> {
        throw InvalidEndpoint()
    }
}