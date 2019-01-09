package test.integration.handlers.user_account

import framework.models.idValue
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import io.kotlintest.Description
import com.amazonaws.services.lambda.runtime.Context
import com.beust.klaxon.JsonObject
import io.kotlintest.TestResult
import kotlinserverless.framework.models.*
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import io.mockk.mockk
import main.daos.UserAccount
import test.TestHelper
import main.helpers.JsonHelper

@ExtendWith(MockKExtension::class)
class GetUserAccountTest : WordSpec() {
    private lateinit var handler: Handler
    private lateinit var contxt: Context
    private lateinit var user: UserAccount
    private lateinit var map: Map<String, Any>

    override fun beforeTest(description: Description): Unit {
        Handler.connectAndBuildTables()
        user = TestHelper.generateUserAccounts().first()
        handler = Handler()
        contxt = mockk()
        map = mutableMapOf(
                Pair("path", "/user_account/"),
                Pair("httpMethod", "GET"),
                Pair("pathParameters", mutableMapOf(
                    Pair("id", user.idValue)
                )),
                Pair("userId", user.idValue.toString())
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
                val newUserAccountJson = JsonHelper.parse(response.body!!)

                newUserAccountJson.containsKey("apiCreds") shouldBe true
                newUserAccountJson.containsKey("session") shouldBe true
                newUserAccountJson.containsKey("cryptoKeyPair") shouldBe true
                newUserAccountJson.containsKey("userMetadata") shouldBe true

                val newUserMetadata = newUserAccountJson["userMetadata"] as JsonObject
                newUserMetadata["email"] shouldBe "dev0@ncnt.io"
            }
        }
    }
}