package kotlinserverless.framework.models

import com.beust.klaxon.Json
import com.fasterxml.jackson.annotation.JsonProperty
import framework.models.BaseObject
import framework.services.DaoService
import java.util.*


/**
 * Instead of having a generic response for everything now the Response class is an interface
 * and we create an specific implementation of it
 */
class ApiGatewayResponse(
        val statusCode: Int = 200,
        var body: Any? = null,
        val headers: Map<String, String>? = Collections.emptyMap(),
        @Json("isBase64Encoded") @JsonProperty("isBase64Encoded") val isBase64Encoded: Boolean = false
): Response {

  /**
   * Uses the Builder pattern to create the response
   */
  class Builder {
    var statusCode: Int = 200
    var rawBody: Any? = null
    var headers: Map<String, String>? = mapOf("X-Powered-By" to "AWS Lambda & Serverless")

    fun build(): ApiGatewayResponse {
      var body = try {
        getBody(rawBody)
      } catch(e: IllegalStateException) {
        Handler.log(e, e.message)
        DaoService.execute {
          getBody(rawBody)
        }.data!!
      }

      if(body != null && body !is String)
        body = Handler.objectMapper.writeValueAsString(body)
      return ApiGatewayResponse(statusCode, body, headers)
    }

    companion object {
      private fun getBody(rawBody: Any?): Any? {
        return when (rawBody) {
          is BaseObject -> {
            (rawBody as? BaseObject)?.toMap()
          }
          is List<*> -> {
            if ((rawBody as? List<*>)?.first() is BaseObject) {
              (rawBody as? List<*>)?.map { (it as? BaseObject)?.toMap() }
            } else {
              (rawBody as? List<*>)?.map { it.toString() }
            }
          }
          else -> rawBody
        }
      }
    }
  }
}