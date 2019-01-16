package test.integration.handlers.challenge

import com.amazonaws.services.lambda.runtime.Context
import com.beust.klaxon.JsonObject
import framework.models.idValue
import io.kotlintest.specs.WordSpec
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import io.kotlintest.Description
import io.kotlintest.shouldBe
import io.kotlintest.TestResult
import io.kotlintest.shouldNotBe
import kotlinserverless.framework.models.Handler
import io.mockk.mockk
import main.daos.*
import main.helpers.JsonHelper
import test.TestHelper
import org.jetbrains.exposed.sql.transactions.transaction

@ExtendWith(MockKExtension::class)
class FindOneChallengeTest : WordSpec() {
    private lateinit var handler: Handler
    private lateinit var contxt: Context
    private lateinit var user: UserAccount
    private lateinit var challenge: Challenge
    private lateinit var map: Map<String, Any>
    private lateinit var badMap: Map<String, Any>

    override fun beforeTest(description: Description) {
        Handler.connectAndBuildTables()
        handler = Handler()
        contxt = mockk()
        transaction {
            user = TestHelper.generateUserAccounts().first().value
            challenge = TestHelper.generateFullChallenge(user, user).first()
        }
        map = mutableMapOf(
                Pair("path", "/challenge/"),
                Pair("httpMethod", "GET"),
                Pair("userId", user.idValue.toString()),
                Pair("pathParameters", mutableMapOf(
                        Pair("id", challenge.idValue)
                ))
        )
        badMap = mutableMapOf(
                Pair("path", "/challenge/"),
                Pair("httpMethod", "GET"),
                Pair("userId", user.idValue.toString()),
                Pair("pathParameters", mutableMapOf(
                        Pair("id", 404)
                ))
        )
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        "calling the API with a valid challenge Id" should {
            "should return a valid challenge" {
                transaction {
                    val findOneChallengeResult = handler.handleRequest(map, contxt)
                    findOneChallengeResult.statusCode shouldBe 200

                    val challengeData = JsonHelper.parse<Map<String, Any>>(findOneChallengeResult.body!!)
                    val challengeSettings = challengeData["challengeSettings"]!! as JsonObject
                    challengeSettings.get("name") shouldBe challenge.challengeSettings.name
                }
            }
        }
        "calling the API with an invalid challenge Id" should {
            "should return a failure response" {
                transaction {
                    val findOneChallengeResult = handler.handleRequest(badMap, contxt)
                    findOneChallengeResult.statusCode shouldBe 404
                }
            }
        }
    }
}