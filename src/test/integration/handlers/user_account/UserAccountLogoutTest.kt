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
import main.daos.UserAccount
import test.TestHelper
import org.jetbrains.exposed.sql.transactions.transaction

@ExtendWith(MockKExtension::class)
class UserAccountLogoutTest : WordSpec() {
    private lateinit var handler: Handler
    private lateinit var contxt: Context
    private lateinit var user: UserAccount
    private val map = mutableMapOf(
            Pair("path", "/user_account/logout"),
            Pair("httpMethod", "PATCH")
    )

    override fun beforeTest(description: Description): Unit {
        Handler.connectAndBuildTables()
        contxt = mockk()
        handler = Handler()
        val newUsers = TestHelper.generateUserAccounts()
        user = newUsers[0].value
        map["secretKey"] = newUsers[0].secretKey
        map["userId"] = user.idValue.toString()
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