package test.integration.handlers.user_account

import framework.models.idValue
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import io.kotlintest.Description
import com.amazonaws.services.lambda.runtime.Context
import com.beust.klaxon.Klaxon
import io.kotlintest.TestResult
import kotlinserverless.framework.models.*
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import io.mockk.mockk
import main.daos.*
import main.helpers.JsonHelper
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import test.TestHelper

@ExtendWith(MockKExtension::class)
class GetUserAccountTest : WordSpec() {
    private lateinit var handler: Handler
    private lateinit var contxt: Context
    private lateinit var user1: UserAccount
    private lateinit var map: Map<String, Any>

    override fun beforeTest(description: Description): Unit {
        Handler.connectAndBuildTables()
        user1 = TestHelper.generateUserAccounts().first()
        transaction {
            val metadataId = Metadatas.insertAndGetId {
                it[key] = "test1key"
                it[value] = "test1val"
            }
            UsersMetadata.insert {
                it[user] = user1.id
                it[metadata] = metadataId
            }
            user1.refresh(true)
        }
        handler = Handler()
        contxt = mockk()
        map = mutableMapOf(
                Pair("path", "/user_account/"),
                Pair("httpMethod", "GET"),
                Pair("pathParameters", mutableMapOf(
                    Pair("id", user1.idValue)
                )),
                Pair("userId", user1.idValue.toString())
        )
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        "correct path" should {
            "should return a valid user account" {
                val response = handler.handleRequest(map, contxt)
                response.statusCode shouldBe 200
                val userAccount: UserAccountNamespace = JsonHelper.parse(response.body!!)!!

                userAccount.userMetadata.email shouldBe "dev0@ncnt.io"
                userAccount.userMetadata.metadatas.first().key shouldBe "test1key"
            }
        }
    }
}