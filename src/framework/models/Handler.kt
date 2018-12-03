package kotlinserverless.framework.models

import kotlinserverless.framework.models.ApiGatewayResponse.Companion.LOG
import kotlinserverless.framework.dispatchers.RequestDispatcher
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import framework.models.BaseIntEntity
import main.daos.*
import org.apache.log4j.Logger
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

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
    lateinit var db: Database
    lateinit var connection: Connection

    fun connectAndBuildTables(): Database {
      db = connectToDatabase()
      dropTables()
      buildTables()
      return db
    }

    fun log(): Logger {
      return LOG
    }

    private fun buildTables() {
      transaction {
        SchemaUtils.create(
            Users,
            CryptoKeyPairs,
            ApiCreds,
            Sessions,
            UserAccounts,
            Actions,
            Transactions,
            Metadatas,
            TransactionsMetadata,
            Tokens,
            TokenTypes,
            Rewards,
            RewardPools,
            RewardTypes,
            RewardsToTransactions,
            RewardsMetadata,
            CompletionCriterias
        )
      }
    }

    fun connectToDatabase(): Database {
      db = Database.connect(
              System.getenv("database_url") ?: "jdbc:h2:mem:test",
              driver = System.getenv("database_driver") ?: "org.h2.Driver",
              user = System.getenv("database_user") ?: "",
              password = System.getenv("database_password") ?: ""
      )
      connection = db.connector.invoke()
      return db
    }

    fun disconnectFromDatabase() {
      connection.close()
    }

    fun disconnectAndDropTables() {
      dropTables()
      disconnectFromDatabase()
    }

    private fun dropTables() {
      transaction {
        SchemaUtils.drop(
            Users,
            CryptoKeyPairs,
            ApiCreds,
            Sessions,
            UserAccounts,
            Actions,
            Transactions,
            Metadatas,
            TransactionsMetadata,
            Tokens,
            TokenTypes,
            Rewards,
            RewardPools,
            RewardTypes,
            RewardsToTransactions,
            RewardsMetadata,
            CompletionCriterias
        )
      }
    }
  }
}