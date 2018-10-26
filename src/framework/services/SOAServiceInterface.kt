package kotlinserverless.framework.services

import framework.models.BaseIntEntity
import kotlinserverless.framework.healthchecks.InvalidEndpoint

interface SOAServiceInterface<T> {
    fun execute() : SOAResult<T> {
        throw InvalidEndpoint()
    }

    fun execute(caller: Int?) : SOAResult<T> {
        throw InvalidEndpoint()
    }

    fun execute(caller: Int?, key: String?) : SOAResult<T> {
        throw InvalidEndpoint()
    }

    fun execute(caller: Int?, key: String?, value: String?) : SOAResult<T> {
        throw InvalidEndpoint()
    }

    // GET/UPDATE a single result by id and filters
    fun execute(caller: Int?, id: Int?, params: Map<String, String>?) : SOAResult<T> {
        throw InvalidEndpoint()
    }

    // GET/UPDATE a single result by key and filters
    fun execute(caller: Int?, key: String?, params: Map<String, String>?) : SOAResult<T> {
        throw InvalidEndpoint()
    }

    // GET/UPDATE multiple results by filters
    fun execute(caller: Int?, params: Map<String, String>?) : SOAResult<List<T>> {
        throw InvalidEndpoint()
    }

    // GET/UPDATE multiple results by filters
    fun execute(caller: Int?, key: String?, value: BaseIntEntity?) : SOAResult<List<T>> {
        throw InvalidEndpoint()
    }

    // CREATE/UPDATE a single object
    fun execute(caller: Int?, data: Any?, params: Map<String, String>?) : SOAResult<T> {
        throw InvalidEndpoint()
    }

    // CREATE/UPDATE multiple objects
    fun execute(caller: Int?, dataList: List<Any>?, params: Map<String, String>?) : SOAResult<List<T>> {
        throw InvalidEndpoint()
    }
}