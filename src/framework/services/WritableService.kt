package kotlinserverless.framework.services

import kotlinserverless.framework.*
import kotlinserverless.framework.healthchecks.models.Healthcheck

/**
 * Service that exposes the capabilities of a {@link T} element
 * @param <K> Natural Key type
 * @param <T> Element type
 * @param <F> Filter type
 * @param <U> User permissions
 */
interface WritableService<T, U> {
    /**
     * Creates a [T] in the system
     * @param user [U] User who is requesting (to verify permissions)
     * @param element [T] that is going to be saved
     */
    fun create(user: U, element: T)

    /**
     * Updates a [T]
     * @param user [U] User who is requesting (to verify permissions)
     * @param element [T] that is going to be updated
     */
    fun update(user: U, element: T)

    /**
     * Deletes a [T] given a unique ID
     * @param user [U] User who is requesting (to verify permissions)
     * @param id Unique ID of the [T]
     */
    fun delete(user: U, id: Int)
}