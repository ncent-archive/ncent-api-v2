package test.integration.handlers.challenge

import com.amazonaws.services.lambda.runtime.Context
import com.beust.klaxon.Klaxon
import framework.models.idValue
import io.kotlintest.shouldBe
import io.kotlintest.Description
import io.kotlintest.TestResult
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.WordSpec
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinserverless.framework.models.Handler
import main.daos.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.extension.ExtendWith
import test.TestHelper

@ExtendWith(MockKExtension::class)
class ChallengeCreationTest : WordSpec() {
    private lateinit var handler: Handler
    private lateinit var contxt: Context
    private lateinit var newUser: NewUserAccount
    private lateinit var challengeNamespace: ChallengeNamespace
    private lateinit var map: Map<String, Any>
    private lateinit var badMap: Map<String, Any>

    override fun beforeTest(description: Description) {
        Handler.connectAndBuildTables()
        handler = Handler(true)
        contxt = mockk()
        transaction {
            val newUsers = TestHelper.generateUserAccounts()
            newUser = newUsers[0]
            challengeNamespace = TestHelper.generateChallengeNamespace(newUser.value).first()
            map = TestHelper.buildRequest(
                newUser,
                "/challenge/",
                "POST",
                mapOf(
                    Pair("challengeNamespace", Klaxon().toJsonString(challengeNamespace))
                )
            )

            badMap = TestHelper.buildRequest(
                newUser,
                "/challenge/",
                "POST",
                mapOf(
                    Pair("challengeNamespace", "null")
                )
            )
        }
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        "correct path" should {
            "should return a valid new challenge" {
                transaction {
                    val response = handler.handleRequest(map, contxt)
                    response.statusCode shouldBe 200
                }
            }
        }

        "calling this API with incorrect parameters" should {
            "should return a failure response" {
                transaction {
                    val response = handler.handleRequest(badMap, contxt)
                    response.statusCode shouldNotBe 200
                }
            }
        }
    }
}