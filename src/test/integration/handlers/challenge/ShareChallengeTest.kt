package test.integration.handlers.challenge

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
import main.daos.*
import main.helpers.JsonHelper
import test.TestHelper
import org.jetbrains.exposed.sql.transactions.transaction

@ExtendWith(MockKExtension::class)
class ShareChallengeTest : WordSpec() {
    private lateinit var handler: Handler
    private lateinit var contxt: Context
    private lateinit var user1: NewUserAccount
    private lateinit var user2: NewUserAccount
    private lateinit var challenge: Challenge
    private lateinit var map: Map<String, Any>
    private lateinit var newUserMap: Map<String, Any>
    private lateinit var tooManySharesMap: Map<String, Any>

    override fun beforeTest(description: Description) {
        Handler.connectAndBuildTables()
        handler = Handler(true)
        contxt = mockk()
        transaction {
            val newUsers = TestHelper.generateUserAccounts(2)
            user1 = newUsers[0]
            user2 = newUsers[1]
            challenge = TestHelper.generateFullChallenge(user1.value, user1.value).first()
            map = TestHelper.buildRequest(
                user1,
                "/challenge/share",
                "PATCH",
                mapOf(
                    Pair("challengeId", challenge.idValue.toString()),
                    Pair("publicKeyToShareWith", user2.value.cryptoKeyPair.publicKey),
                    Pair("shares", "3"),
                    Pair("emailToShareWith", user2.value.userMetadata.email)
                ),
                mapOf(
                    Pair("userId", user1.value.idValue.toString())
                )
            )
            newUserMap = TestHelper.buildRequest(
                user1,
                "/challenge/share",
                "PATCH",
                mapOf(
                    Pair("challengeId", challenge.idValue.toString()),
                    Pair("emailToShareWith", "test@test.com"),
                    Pair("shares", "3")
                ),
                    mapOf(
                            Pair("userId", user1.value.idValue.toString())
                    )
            )
            tooManySharesMap = TestHelper.buildRequest(
                user1,
                "/challenge/share",
                "PATCH",
                mapOf(
                    Pair("challengeId", challenge.idValue.toString()),
                    Pair("publicKeyToShareWith", user2.value.cryptoKeyPair.publicKey),
                    Pair("emailToShareWith", user2.value.userMetadata.email),
                    Pair("shares", "1000")
                ),
                    mapOf(
                            Pair("userId", user1.value.idValue.toString())
                    )
            )
        }
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        "correct path" should {
            "should return a successful transaction" {
                transaction {
                    val response = handler.handleRequest(map, contxt)
                    response.statusCode shouldBe 200

                    val transactionWithNewUserNamespace = JsonHelper.parse<TransactionWithNewUserNamespace>(response.body.toString())

                    transactionWithNewUserNamespace.transactions.first().from shouldBe user1.value.cryptoKeyPair.publicKey
                    transactionWithNewUserNamespace.transactions.first().to shouldBe user2.value.cryptoKeyPair.publicKey
                }
            }
        }

        "valid API call" should {
            "should share successfully with new user" {
                transaction {
                    val response = handler.handleRequest(newUserMap, contxt)
                    response.statusCode shouldBe 200
                }
            }
        }

        "call made with too many shares" should {
            "should return a failure response" {
                transaction {
                    val response = handler.handleRequest(tooManySharesMap, contxt)
                    response.statusCode shouldBe 403
                    response.body shouldBe "You do not have enough valid shares available: 100"
                }
            }
        }
    }
}