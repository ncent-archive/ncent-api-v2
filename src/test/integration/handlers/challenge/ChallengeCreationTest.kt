package test.integration.handlers.challenge

import com.amazonaws.services.lambda.runtime.Context
import com.beust.klaxon.Klaxon
import framework.models.idValue
import io.kotlintest.shouldBe
import io.kotlintest.Description
import io.kotlintest.TestResult
import io.kotlintest.specs.WordSpec
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinserverless.framework.models.Handler
import main.daos.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.extension.ExtendWith
import test.TestHelper
import java.lang.StringBuilder

@ExtendWith(MockKExtension::class)
class ChallengeCreationTest : WordSpec() {
    private lateinit var handler: Handler
    private lateinit var contxt: Context
    private lateinit var user: UserAccount
    private lateinit var newUser: NewUserAccount
    private lateinit var challengeNamespace: ChallengeNamespace
    private lateinit var map: Any

    override fun beforeTest(description: Description) {
        Handler.connectAndBuildTables()
        handler = Handler()
        contxt = mockk()
        transaction {
            val newUsers = TestHelper.generateUserAccounts()
            newUser = newUsers[0]
            user = newUser.value
            challengeNamespace = TestHelper.generateChallengeNamespace(user).first()
            map = mutableMapOf(
                    Pair("path", "/challenge/"),
                    Pair("httpMethod", "POST"),
                    Pair("userId", user.idValue.toString()),
                    Pair("body", mapOf(
                            Pair("challengeNamespace", Klaxon().toJsonString(challengeNamespace)),
                            Pair("secretKey", newUser.secretKey)
                    ))
            )
        }
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        "correct path" should {
            "should return a valid new challenge" {
                val response = handler.handleRequest(map as Map<String, Any>, contxt)
                response.statusCode shouldBe 200
            }
        }
    }
}