package kotlinserverless.framework.controllers

import kotlinserverless.framework.healthchecks.InvalidEndpoint
import main.daos.Healthcheck
import kotlinserverless.framework.services.SOAResult
import main.helpers.ControllerHelper.RequestData

/**
 * Service that exposes the capabilities of a {@link T} element
 * @param <K> Natural Key type
 * @param <T> Element type
 * @param <F> Filter type
 * @param <U> User permissions
 */
interface ReadableController<T, U> {
//    /**
//     * Find a set of [T] by a given set of optional parameters
//     * @param user [U] User who is requesting (to verify permissions)
//     * @param filters Optional parameters
//     * @param pagination How to paginate the result
//     * @return list of [T]
//     */
//    fun findAll(user: U, filters: Map<String, Any> = mapOf( "order" to "creationDate" ),
//                pagination: Pagination = Pagination(0, 50)): SOAResult<Page<T>>{
//        throw InvalidEndpoint()
//    }

    fun findAll(user: U, requestData: RequestData): SOAResult<List<T>> {
        throw InvalidEndpoint()
    }

    /**
     * Finds one [T] by the unique ID
     * @param user [U] User who is requesting (to verify permissions)
     * @param id Unique id
     * @return [T] that has that ID
     */
    fun findOne(user: U, requestData: RequestData, id: Int?): SOAResult<T> {
        throw InvalidEndpoint()
    }

    /**
     * Returns the amount or [T] entities in the system
     * @param user [U] User who is requesting (to verify permissions)
     * @param filters Set of filters
     * @return list of [T]
     */
    fun count(user: U, requestData: RequestData): SOAResult<Int> {
        throw InvalidEndpoint()
    }

    /**
     * Verifies if a entity with a specific ID exists
     * @param user [U] User who is requesting (to verify permissions)
     * @param id Unique id
     * @return [Boolean] value indicating true if exists or false if not
     */
    fun exists(user: U, requestData: RequestData): SOAResult<Boolean> {
        throw InvalidEndpoint()
    }

    /**
     * Provide a basic healthcheck for this object type and function
     * ex: used to verify access to the database/cache layer is functioning properly
     * @return [Healthcheck] object representing the health
     */
    fun health(user: U?, requestData: RequestData): SOAResult<Healthcheck> {
        throw InvalidEndpoint()
    }
}