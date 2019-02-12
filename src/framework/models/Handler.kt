package kotlinserverless.framework.models

import kotlinserverless.framework.dispatchers.RequestDispatcher
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.bugsnag.Bugsnag
import com.fasterxml.jackson.databind.ObjectMapper
import main.daos.*
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.LogManager
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

open class Handler: RequestHandler<Map<String, Any>, ApiGatewayResponse> {

  var requestDispatcher: RequestDispatcher = RequestDispatcher()

  constructor() {
    BasicConfigurator.configure()
    connectToDatabase()
    buildTables()
  }

  constructor(test: Boolean = false) {
    if(!test!!) {
      BasicConfigurator.configure()
    }
    connectToDatabase()
    buildTables(test)
  }

  fun getDbName(): String {
    return db.url
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
    catch (e: MyException) {
      log(e, e.message)
      status = e.code
      body = e.message
    }
    catch (e: Throwable) {
      log(e, e.message)
      status = 500
      body = "Internal server error " + e.message.toString() + "\n" + e.stackTrace.map { "\n"+it.toString() }
    }
    finally {
      return build {
        statusCode = status
        rawBody = body
      }
    }
  }

  companion object {
    lateinit var db: Database
    lateinit var connection: Connection

    inline fun build(block: ApiGatewayResponse.Builder.() -> Unit) = ApiGatewayResponse.Builder().apply(block).build()
    private val LOG = LogManager.getLogger(this::class.java)!!
    val objectMapper = ObjectMapper()

    private var bugsnagInstance: Bugsnag? = null
    private fun bugsnag(): Bugsnag {
      if(bugsnagInstance == null) {
        bugsnagInstance = Bugsnag(System.getenv("bugsnag_api_key") ?: "local")
        bugsnagInstance!!.setNotifyReleaseStages("production", "development")
        bugsnagInstance!!.setReleaseStage(System.getenv("release_stage") ?: "local")
      }
      return bugsnagInstance!!
    }

    private val TABLES = arrayOf(
      Healthchecks,
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
      PrerequisiteChallenge,
      Rewards,
      RewardPools,
      RewardTypes,
      RewardsToTransactions,
      RewardsMetadata,
      CompletionCriterias,
      Challenges,
      ChallengeSettings,
      SubChallenges,
      ChallengeToSubChallenges,
      UsersMetadata
    )

    fun log(e: Throwable?, message: String?) {
      if(e != null) {
        LOG.error(message, e)
        bugsnag().notify(e)
      } else if(message != null) {
        LOG.info(message)
      }
    }

    fun connectAndBuildTables(): Database {
      db = connectToDatabase()
      dropTables()
      buildTables()
      return db
    }

    private fun buildTables(test: Boolean = false) {
      transaction {
        SchemaUtils.create(*TABLES)
        if(!test) {
          try {
            Healthchecks.insertIgnore {
              it[status] = "database_healthy"
              it[message] = "connected to database."
            }
            Healthchecks.insertIgnore {
              it[status] = "database_unhealthy"
              it[message] = "failed to connect to database."
            }
          } catch(e: ExposedSQLException) {

          }
        }
      }
    }

    fun connectToDatabase(): Database {
      try {
        db = Database.connect(
                System.getenv("database_url") ?: "jdbc:h2:mem:test;MODE=MySQL",
                driver = System.getenv("database_driver") ?: "org.h2.Driver",
                user = System.getenv("database_user") ?: "",
                password = System.getenv("database_password") ?: ""
        )
        connection = db.connector.invoke()
      } catch(e: Exception) {
        println(e.message)
      }
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
        SchemaUtils.drop(*TABLES)
      }
    }
  }
}