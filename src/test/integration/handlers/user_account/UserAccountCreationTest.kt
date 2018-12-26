package test.integration.handlers.user_account

import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import io.kotlintest.Description
import com.amazonaws.services.lambda.runtime.Context
import kotlinserverless.framework.models.*
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import io.mockk.mockk
import main.daos.User

@ExtendWith(MockKExtension::class)
class UserAccountCreationTest : WordSpec() {
    private lateinit var handler: Handler
    private lateinit var contxt: Context
    private lateinit var user: User
    private lateinit var map: Map<String, Any>

    override fun beforeTest(description: Description): Unit {
        Handler.connectAndBuildTables()
        user = mockk()
        contxt = mockk()
        map = mapOf(
            Pair("path", "user_account"),
            Pair("httpMethod", "POST")
        )
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
