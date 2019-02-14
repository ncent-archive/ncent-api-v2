package test.integration.handlers.user_account

import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import io.kotlintest.Description
import com.amazonaws.services.lambda.runtime.Context
import io.kotlintest.TestResult
import kotlinserverless.framework.models.*
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import io.mockk.mockk
import main.daos.*
import main.helpers.JsonHelper
import org.jetbrains.exposed.sql.transactions.transaction
import test.TestHelper

@ExtendWith(MockKExtension::class)
class DeleteUserAccountTest : WordSpec() {
    private lateinit var handler: Handler
    private lateinit var contxt: Context
    private lateinit var user1: NewUserAccount
    private lateinit var map: Map<String, Any>

    override fun beforeTest(description: Description): Unit {
        Handler.connectAndBuildTables()
        val newUsers = TestHelper.generateUserAccounts(1)
        user1 = newUsers[0]
        transaction {
            handler = Handler(true)
            contxt = mockk()
            map = TestHelper.buildRequest(
                user1,
                "/user",
                "DELETE",
                null
            )
        }
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        "correct path" should {
            "should delete the user and return success" {
                transaction {
                    val response = handler.handleRequest(map, contxt)
                    response.statusCode shouldBe 204
                }
            }
        }
    }
}