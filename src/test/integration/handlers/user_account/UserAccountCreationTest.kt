package test.integration.handlers.user_account

import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import io.kotlintest.Description
import com.amazonaws.services.lambda.runtime.Context
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import framework.models.idValue
import io.kotlintest.TestResult
import kotlinserverless.framework.models.*
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import io.mockk.mockk
import main.daos.UserAccount
import test.TestHelper

@ExtendWith(MockKExtension::class)
class UserAccountCreationTest : WordSpec() {
    private lateinit var handler: Handler
    private lateinit var contxt: Context
    private lateinit var user: UserAccount
    private val map = mutableMapOf(
        Pair("path", "/user_account/"),
        Pair("httpMethod", "POST"),
        Pair("body", mapOf(
            Pair("email", "dev@ncnt.io"),
            Pair("firstname", "dev"),
            Pair("lastname", "ncnt")
        ))
    )

    override fun beforeTest(description: Description): Unit {
        Handler.connectAndBuildTables()
        handler = Handler()
        contxt = mockk()
        val users = TestHelper.generateUserAccounts()
        user = users[users.keys.first()]!!
        map["userId"] = user.idValue.toString()
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        "correct path" should {
            "should return a valid new user account" {
                val response = handler.handleRequest(map, contxt)
                response.statusCode shouldBe 200
                val newUserAccountMap = Klaxon().parse<Map<String?, Any>>(response.body!!)
                val newUserAccountDataMap = Klaxon().parse<Map<String?, Any>>(newUserAccountMap?.get("value") as String)

                newUserAccountDataMap!!.containsKey("apiCreds") shouldBe true
                newUserAccountDataMap.containsKey("session") shouldBe true
                newUserAccountDataMap.containsKey("cryptoKeyPair") shouldBe true
                newUserAccountDataMap.containsKey("userMetadata") shouldBe true

                val newUserMetadata = newUserAccountDataMap["userMetadata"] as JsonObject
                newUserMetadata["email"] shouldBe "dev@ncnt.io"
            }
        }
    }
}
