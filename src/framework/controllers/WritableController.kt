package kotlinserverless.framework.controllers

import kotlinserverless.framework.healthchecks.InvalidEndpoint
import kotlinserverless.framework.services.SOAResult

/**
 * Service that exposes the capabilities of a {@link T} element
 * @param <K> Natural Key type
 * @param <T> Element type
 * @param <F> Filter type
 * @param <U> User permissions
 */
interface WritableController<T, U> {
    /**
     * Creates a [T] in the system
     * @param user [U] User who is requesting (to verify permissions)
     * @param element [T] that is going to be saved
     */
    fun create(user: U, params: Map<String, String>): SOAResult<*> {
        throw InvalidEndpoint()
    }

    /**
     * Updates a [T]
     * @param user [U] User who is requesting (to verify permissions)
     * @param element [T] that is going to be updated
     */
    fun update(user: U, params: Map<String, String>): SOAResult<T> {
        throw InvalidEndpoint()
    }

    /**
     * Deletes a [T] given a unique ID
     * @param user [U] User who is requesting (to verify permissions)
     * @param id Unique ID of the [T]
     */
    fun delete(user: U, id: Int): SOAResult<T> {
        throw InvalidEndpoint()
    }
}