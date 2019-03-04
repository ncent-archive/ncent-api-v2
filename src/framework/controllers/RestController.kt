package kotlinserverless.framework.controllers

/**
 * Service that exposes the capabilities of a {@link T} element
 * @param <K> Natural Key type
 * @param <T> Element type
 * @param <F> Filter type
 * @param <U> User permissions
 */
interface RestController<T, U> : ReadableController<T, U>, WritableController<T, U>