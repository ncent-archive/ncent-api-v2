package kotlinserverless.framework.services

data class SOAResult<T>(var result: SOAResultType, var message: String?, var data: T? = null)

enum class SOAResultType {
    SUCCESS,
    FAILURE,
    ERROR
}