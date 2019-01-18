package main.helpers

import kotlinserverless.framework.models.InvalidArguments
import kotlinserverless.framework.models.Pagination
import kotlinserverless.framework.models.Request
import org.glassfish.jersey.internal.util.Base64
import kotlin.math.ceil

object ControllerHelper {

    // HTTP constants
    const val HTTP_METHOD: String = "httpMethod"
    const val HTTP_GET: String = "get"
    const val HTTP_POST: String = "post"
    const val HTTP_PUT: String = "put"
    const val HTTP_DELETE: String = "delete"
    const val HTTP_PATCH: String = "patch"

    // Pagination constants
    const val LIMIT: Int = 50
    const val MAX_LIMIT: Int = 100
    const val OFFSET: Int = 0
    const val MIN_LIMIT: Int = 0

    private fun anyToInt(value: Any, valueType: String = "page"): Int {
        return when (value) {
            is String -> value.toIntOrNull() ?: throw Exception("$valueType must be a number")
            is Int -> value
            else -> throw Exception("$valueType must be a number")
        }
    }

    private fun getStringAnyMap(request: Request, key: String): Map<String, Any> {
        return if (request.input.containsKey(key) && request.input[key] != null)
            request.input[key] as Map<String, Any>
        else
            emptyMap()
    }

    private fun getResource(request: Request): String? {
        return request.input["resource"] as String?
    }

    private fun getHeaders(request: Request) : Map<String, Any> {
        return getStringAnyMap(request, "headers")
    }

    private fun getPathParameters(request: Request) : Map<String, Any> {
        return getStringAnyMap(request, "pathParameters")
    }

    private fun getQueryStringParameters(request: Request) : Map<String, Any> {
        return getStringAnyMap(request, "queryStringParameters")
    }

    private fun getBody(request: Request) : Map<String, Any> {
        return getStringAnyMap(request, "body")
    }

    private fun getPagination(queryParameters: Map<String, Any>): Pagination {
        var page: Int = ceil(OFFSET.toDouble() / LIMIT).toInt()
        var size: Int = LIMIT
        val pagination = Pagination(page, size)

        if (queryParameters.containsKey("page")) {
            page = anyToInt(queryParameters["page"]!!)
            pagination.page = if (page >= MIN_LIMIT + 1) page - 1 else MIN_LIMIT
        }

        if (queryParameters.containsKey("size")) {
            size = anyToInt(queryParameters["size"]!!, "size")
            pagination.size = if (size < MAX_LIMIT) size else MAX_LIMIT
        }

        return pagination

    }

    private fun getUserAuth(headers: Map<String, Any>): UserAuth? {
        if(!headers.containsKey("Authorization: Basic "))
            return null
        val base64EncodedAuth = headers.get("Authorization: Basic ") as String
        val base64DecodedAuth = Base64.decode(base64EncodedAuth.toByteArray())
        val keyAndSecret = base64DecodedAuth.toString().split(":".toRegex(), 2)
        if(keyAndSecret.size != 2)
            throw InvalidArguments("The user authentication parameters are not formatted correctly. Should be apikey:secret")
        return UserAuth(keyAndSecret[0], keyAndSecret[1])
    }

    fun getRequestData(request: Request): RequestData {
        val queryParams = getQueryStringParameters(request)
        val headers = getHeaders(request)
        return RequestData(
            getResource(request),
            headers,
            getPathParameters(request),
            queryParams,
            getBody(request),
            getPagination(queryParams),
            getUserAuth(headers)
        )
    }

    data class RequestData(
        val resource: String?,
        val headers: Map<String, Any>,
        val pathParams: Map<String, Any>,
        val queryParams: Map<String, Any>,
        val body: Map<String, Any>,
        val pagination: Pagination,
        val userAuth: UserAuth?
    )

    data class UserAuth(
        val apiKey: String,
        val secretKey: String
    )
}