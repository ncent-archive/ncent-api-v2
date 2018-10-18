package kotlinserverless.framework.models

import kotlinserverless.framework.models.ApiGatewayResponse.Companion.LOG
import kotlinserverless.framework.dispatchers.RequestDispatcher
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import framework.models.BaseIntEntity
import main.daos.User
import org.jetbrains.exposed.sql.Database

open class Handler: RequestHandler<Map<String, Any>, ApiGatewayResponse> {

  var requestDispatcher: RequestDispatcher = RequestDispatcher()
  var defaultUser: User

  constructor() {
    connectToDatabase()

    defaultUser = User.new {
      email = "default"
      firstname = "default"
      lastname = "default"
    }
    this.requestDispatcher.defaultUser = defaultUser
  }

  constructor(user: User) {
    connectToDatabase()
    this.requestDispatcher.defaultUser = user
    this.defaultUser = user
  }

  override fun handleRequest(input: Map<String, Any>, context: Context): ApiGatewayResponse {

    var status = 500
    var body: Any? = null

    try {
      body = requestDispatcher.locate(ApiGatewayRequest(input, context))

      status = when (body == null) {
        true -> 204
        false -> 200
      }
    }
    catch(e: RouterException) {
      LOG.error(e.message, e)
      status = 404
      body = e.message
    }
    catch (e: MyException) {
      LOG.error(e.message, e)
      status = e.code
      body = e.message
    }
    catch (e: Throwable) {
      LOG.error(e.message, e)
      status = 500
      body = "Internal server error"
    }
    finally {
      return ApiGatewayResponse.build {
        statusCode = status
        if (body is BaseIntEntity)
          objectBody = body
        else if (body is Collection<*>)
          listBody = body as List<Any>
	    else
		  rawBody = body.toString()
        headers = mapOf("X-Powered-By" to "AWS Lambda & Serverless")
      }
    }
  }

  companion object {
    val db: Database = connectToDatabase()

    fun connectToDatabase(): Database {
      return Database.connect(
            System.getenv("database_url") ?: "jdbc:h2:mem:test",
            driver = System.getenv("database_driver") ?: "org.h2.Driver",
            user = System.getenv("database_user") ?: "",
            password = System.getenv("database_password") ?: ""
      )
    }
  }
}