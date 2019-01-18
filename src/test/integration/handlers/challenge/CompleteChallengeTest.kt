package test.integration.handlers.challenge

import com.amazonaws.services.lambda.runtime.Context
import framework.models.idValue
import io.kotlintest.specs.WordSpec
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import io.kotlintest.Description
import io.kotlintest.shouldBe
import io.kotlintest.TestResult
import kotlinserverless.framework.models.Handler
import io.mockk.mockk
import main.daos.*
import main.helpers.JsonHelper
import main.services.challenge.ActivateChallengeService
import main.services.challenge.ShareChallengeService
import test.TestHelper
import org.jetbrains.exposed.sql.transactions.transaction

@ExtendWith(MockKExtension::class)
class CompleteChallengeTest : WordSpec() {
    private lateinit var handler: Handler
    private lateinit var contxt: Context
    private lateinit var user1: UserAccount
    private lateinit var user2: UserAccount
    private lateinit var challenge: Challenge
    private lateinit var notActivatedChallenge: Challenge
    private lateinit var map: Map<String, Any>
    private lateinit var notActivatedMap: Map<String, Any>

    override fun beforeTest(description: Description) {
        Handler.connectAndBuildTables()
        handler = Handler()
        contxt = mockk()
        transaction {
            val newUsers = TestHelper.generateUserAccounts(2)
            user1 = newUsers[0].value
            user2 = newUsers[1].value
            challenge = TestHelper.generateFullChallenge(user1, user1, 1, true).first()
            notActivatedChallenge = TestHelper.generateFullChallenge(user1, user1, 1, true).first()
            ActivateChallengeService.execute(user1, challenge.idValue)
            ShareChallengeService.execute(user1, challenge, 2, user2.cryptoKeyPair.publicKey, null, null)
            ShareChallengeService.execute(user1, notActivatedChallenge, 2, user2.cryptoKeyPair.publicKey, null, null)
            map = mutableMapOf(
                    Pair("path", "/challenge/complete"),
                    Pair("httpMethod", "PATCH"),
                    Pair("userId", user1.idValue.toString()),
                    Pair("secretKey", newUsers[0].secretKey),
                    Pair("completerPublicKey", user2.cryptoKeyPair.publicKey),
                    Pair("challengeId", challenge.idValue)
            )
            notActivatedMap = mutableMapOf(
                    Pair("path", "/challenge/complete"),
                    Pair("httpMethod", "PATCH"),
                    Pair("userId", user1.idValue.toString()),
                    Pair("secretKey", newUsers[0].secretKey),
                    Pair("completerPublicKey", user2.cryptoKeyPair.publicKey),
                    Pair("challengeId", notActivatedChallenge.idValue)
            )

        }
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        "Calling the API with valid parameters" should {
            "should return the challenge that was completed" {
                transaction {
                    val completeChallengeResult = handler.handleRequest(map, contxt)
                    completeChallengeResult.statusCode shouldBe 200

                    val transactionNamespaceList = JsonHelper.parse<TransactionNamespaceList>(completeChallengeResult.body!!)
                    transactionNamespaceList.transactions.size shouldBe 2
                }
            }
        }

        "Calling the API on a challenge that has not been activated" should {
            "should return a 403 forbidden response" {
                val completeChallengeResult = handler.handleRequest(notActivatedMap, contxt)
                completeChallengeResult.statusCode shouldBe 403
                completeChallengeResult.body shouldBe "Cannot transition from create to complete"
            }
        }
    }
}