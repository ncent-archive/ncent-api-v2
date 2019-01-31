package kotlinserverless.framework.models

import com.beust.klaxon.Json
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import framework.models.BaseIntEntity
import framework.models.BaseObject
import framework.services.DaoService
import org.apache.log4j.LogManager
import org.jetbrains.exposed.sql.transactions.transaction
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
  companion object {
    inline fun build(block: Builder.() -> Unit) = Builder().apply(block).build()
    val LOG = LogManager.getLogger(this::class.java) //TODO: figure out how to user the correct class name.
    val objectMapper = ObjectMapper()
  }

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
        DaoService.execute {
          getBody(rawBody)
        }.data!!
      }

      if(body != null && body !is String)
        body = objectMapper.writeValueAsString(body)
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