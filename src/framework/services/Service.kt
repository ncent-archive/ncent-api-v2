package kotlinserverless.framework.services

/**
 * Service that exposes the capabilities of a {@link T} element
 * @param <K> Natural Key type
 * @param <T> Element type
 * @param <F> Filter type
 * @param <U> User permissions
 */
interface Service<T, U> : ReadableService<T, U>, WritableService<T, U>