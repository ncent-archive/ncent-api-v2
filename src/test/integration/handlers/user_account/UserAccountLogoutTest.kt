package test.integration.handlers.user_account

import io.kotlintest.shouldBe
import framework.models.idValue
import io.kotlintest.specs.WordSpec
import io.kotlintest.Description
import com.amazonaws.services.lambda.runtime.Context
import io.kotlintest.TestResult
import kotlinserverless.framework.models.*
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import io.mockk.mockk
import main.daos.NewUserAccount
import main.daos.UserAccount
import test.TestHelper
import org.jetbrains.exposed.sql.transactions.transaction

@ExtendWith(MockKExtension::class)
class UserAccountLogoutTest : WordSpec() {
    private lateinit var handler: Handler
    private lateinit var contxt: Context
    private lateinit var user: NewUserAccount
    private lateinit var map: Map<String, Any>

    override fun beforeTest(description: Description): Unit {
        Handler.connectAndBuildTables()
        handler = Handler()
        contxt = mockk()
        transaction {
            val newUsers = TestHelper.generateUserAccounts()
            user = newUsers[0]
            map = TestHelper.buildRequest(
                user,
                "/user_account/logout",
                "PATCH",
                mapOf(
                    Pair("userId", user.value.idValue),
                    Pair("firstname", "dev"),
                    Pair("lastname", "ncnt")
                )
            )
        }
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        "correct path" should {
            "should return a valid user account" {
                transaction {
                    val response = handler.handleRequest(map, contxt)
                    response.statusCode shouldBe 200
                }
            }
        }
    }
}