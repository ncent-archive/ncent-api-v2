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
    private lateinit var user1: UserAccount
    private lateinit var user2: UserAccount
    private lateinit var challenge: Challenge
    private lateinit var map: Map<String, Any>
    private lateinit var newUserMap: Map<String, Any>
    private lateinit var tooManySharesMap: Map<String, Any>

    override fun beforeTest(description: Description) {
        Handler.connectAndBuildTables()
        handler = Handler()
        contxt = mockk()
        transaction {
            val newUsers = TestHelper.generateUserAccounts(2)
            user1 = newUsers[0].value
            user2 = newUsers[1].value
            challenge = TestHelper.generateFullChallenge(user1, user1).first()
            map = mutableMapOf(
                    Pair("path", "/challenge/share"),
                    Pair("httpMethod", "PATCH"),
                    Pair("userId", user1.idValue.toString()),
                    Pair("challengeId", challenge.idValue),
                    Pair("publicKeyToShareWith", user2.cryptoKeyPair.publicKey),
                    Pair("shares", 3),
                    Pair("emailToShareWith", user2.userMetadata.email),
                    Pair("secretKey", newUsers[0].secretKey)
            )
            newUserMap = mutableMapOf(
                    Pair("path", "/challenge/share"),
                    Pair("httpMethod", "PATCH"),
                    Pair("userId", user1.idValue.toString()),
                    Pair("challengeId", challenge.idValue),
                    Pair("shares", 3),
                    Pair("emailToShareWith", "test@test.com"),
                    Pair("secretKey", newUsers[0].secretKey)
            )
            tooManySharesMap = mutableMapOf(
                    Pair("path", "/challenge/share"),
                    Pair("httpMethod", "PATCH"),
                    Pair("userId", user1.idValue.toString()),
                    Pair("challengeId", challenge.idValue),
                    Pair("publicKeyToShareWith", user2.cryptoKeyPair.publicKey),
                    Pair("shares", 1000),
                    Pair("emailToShareWith", user2.userMetadata.email),
                    Pair("secretKey", newUsers[0].secretKey)
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

                    val transactionWithNewUserNamespace = JsonHelper.parse<TransactionWithNewUserNamespace>(response.body as String)

                    transactionWithNewUserNamespace.transactions.first().from shouldBe user1.cryptoKeyPair.publicKey
                    transactionWithNewUserNamespace.transactions.first().to shouldBe user2.cryptoKeyPair.publicKey
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