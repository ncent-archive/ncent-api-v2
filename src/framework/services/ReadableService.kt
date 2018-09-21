package kotlinserverless.framework.services

import kotlinserverless.framework.healthchecks.InvalidEndpoint
import kotlinserverless.framework.models.*
import kotlinserverless.framework.healthchecks.models.Healthcheck

/**
 * Service that exposes the capabilities of a {@link T} element
 * @param <K> Natural Key type
 * @param <T> Element type
 * @param <F> Filter type
 * @param <U> User permissions
 */
interface ReadableService<T, U> {
    /**
     * Find a set of [T] by a given set of optional parameters
     * @param user [U] User who is requesting (to verify permissions)
     * @param filters Optional parameters
     * @param pagination How to paginate the result
     * @return list of [T]
     */
    fun findAll(user: U, filters: Map<String, Any> = mapOf( "order" to "creationDate" ),
                pagination: Pagination = Pagination(0, 50)): Page<T> {
        throw InvalidEndpoint()
    }

    /**
     * Finds one [T] by the unique ID
     * @param user [U] User who is requesting (to verify permissions)
     * @param id Unique id
     * @return [T] that has that ID
     */
    fun findOne(user: U, id: Int): T {
        throw InvalidEndpoint()
    }

    /**
     * Finds one [T] by an unique natural [K] key
     * @param user [U] User who is requesting (to verify permissions)
     * @param filters Set of filters that will return a unique value
     * @return [T] that has that [Any] natural key
     */
    fun findOne(user: U, filters: Map<String, Any> = emptyMap()): T {
        throw InvalidEndpoint()
    }

    /**
     * Returns the amount or [T] entities in the system
     * @param user [U] User who is requesting (to verify permissions)
     * @param filters Set of filters
     * @return list of [T]
     */
    fun count(user: U, filters: Map<String, Any> = emptyMap()): Int {
        throw InvalidEndpoint()
    }

    /**
     * Verifies if a entity with a specific ID exists
     * @param user [U] User who is requesting (to verify permissions)
     * @param id Unique id
     * @return [Boolean] value indicating true if exists or false if not
     */
    fun exists(user: U, id: Int): Boolean {
        throw InvalidEndpoint()
    }

    /**
     * Provide a basic healthcheck for this object type and function
     * ex: used to verify access to the database/cache layer is functioning properly
     * @return [Healthcheck] object representing the health
     */
    fun health(user: U, request: Request?): Healthcheck {
        throw InvalidEndpoint()
    }
}