package kotlinserverless.test.integration.handlers

import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import io.kotlintest.Description
import com.amazonaws.services.lambda.runtime.Context
import framework.models.idValue
import io.kotlintest.TestResult
import kotlinserverless.framework.models.*
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import io.mockk.mockk
import main.daos.UserAccount
import test.TestHelper

@ExtendWith(MockKExtension::class)
class UserIntegrationTest : WordSpec() {
    private lateinit var handler: Handler
    private lateinit var contxt: Context
    private val map = mutableMapOf("path" to "/user/hello")

    override fun beforeTest(description: Description): Unit {
        Handler.connectAndBuildTables()
        handler = Handler()
        contxt = mockk()
        val user = TestHelper.generateUserAccounts().first()
        map["userId"] = user.idValue.toString()
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        "correct path" should {
            "should return HELLO WORLD in the response body" {
                val response = contxt.let { handler.handleRequest(map, it) }
                response.statusCode shouldBe 200
            }
        }
    }
}
