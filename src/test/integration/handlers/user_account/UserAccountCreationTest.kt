package test.integration.handlers.user_account

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
import main.daos.NewUserAccountNamespace
import test.TestHelper

@ExtendWith(MockKExtension::class)
class UserAccountCreationTest : WordSpec() {
    private lateinit var handler: Handler
    private lateinit var contxt: Context
    private val map = TestHelper.buildRequest(
        null,
        "/user_account",
        "POST",
        mapOf(
            Pair("email", "dev@ncnt.io"),
            Pair("firstname", "dev"),
            Pair("lastname", "ncnt")
        )
    )

    override fun beforeTest(description: Description): Unit {
        Handler.connectAndBuildTables()
        handler = Handler()
        contxt = mockk()
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        "correct path" should {
            "should return a valid new user account" {
                val response = handler.handleRequest(map, contxt)
                response.statusCode shouldBe 200
                val newUserAccount = Klaxon().parse<NewUserAccountNamespace>(response.body!!)

                newUserAccount!!.value.userMetadata.email shouldBe "dev@ncnt.io"
            }
        }
    }
}
