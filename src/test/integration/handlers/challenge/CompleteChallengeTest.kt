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
    private lateinit var newUsers: List<NewUserAccount>
    private lateinit var user1: UserAccount
    private lateinit var user2: UserAccount
    private lateinit var challenge: Challenge
    private lateinit var notActivatedChallenge: Challenge
    private lateinit var map: MutableMap<String, Any>

    override fun beforeTest(description: Description) {
        Handler.connectAndBuildTables()
        handler = Handler()
        contxt = mockk()
        transaction {
            newUsers = TestHelper.generateUserAccounts(2)
            user1 = newUsers[0].value
            user2 = newUsers[1].value
            challenge = TestHelper.generateFullChallenge(user1, user1, 1, true).first()
            notActivatedChallenge = TestHelper.generateFullChallenge(user1, user1, 1, true).first()
            ActivateChallengeService.execute(user1, challenge.idValue)
            ShareChallengeService.execute(user1, challenge, 2, user2.cryptoKeyPair.publicKey, null, null)
            ShareChallengeService.execute(user1, notActivatedChallenge, 2, user2.cryptoKeyPair.publicKey, null, null)
            map = mutableMapOf(
                    Pair("httpMethod", "PATCH")
            )
        }
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        "Calling the Complete Challenge API" should {
            "should return the list of reward distribution transactions when passed valid parameters" {
                transaction {
                    map.put("path", "/challenge/complete")
                    map.put("userId", user1.idValue.toString())
                    map.put("secretKey", newUsers[0].secretKey)
                    map.put("completerPublicKey", user2.cryptoKeyPair.publicKey)
                    map.put("challengeId", challenge.idValue)

                    val completeChallengeResult = handler.handleRequest(map, contxt)
                    completeChallengeResult.statusCode shouldBe 200

                    val transactionNamespaceList = JsonHelper.parse<TransactionNamespaceList>(completeChallengeResult.body!!)
                    transactionNamespaceList.transactions.size shouldBe 2
                }

            }
            "should return a 403 forbidden response when attempted by a public key other than the sponsor's" {
                transaction {
                    map.put("path", "/challenge/complete")
                    map.put("userId", user2.idValue.toString())
                    map.put("secretKey", newUsers[0].secretKey)
                    map.put("completerPublicKey", user2.cryptoKeyPair.publicKey)
                    map.put("challengeId", challenge.idValue)

                    val completeChallengeResult = handler.handleRequest(map, contxt)
                    completeChallengeResult.statusCode shouldBe 403
                    completeChallengeResult.body shouldBe "This user cannot change the challenge state"
                }
            }
            "should return a 403 forbidden response when challenge state cannot change to complete" {
                transaction {
                    map.put("path", "/challenge/complete")
                    map.put("userId", user1.idValue.toString())
                    map.put("secretKey", newUsers[0].secretKey)
                    map.put("completerPublicKey", user2.cryptoKeyPair.publicKey)
                    map.put("challengeId", notActivatedChallenge.idValue)

                    val completeChallengeResult = handler.handleRequest(map, contxt)
                    completeChallengeResult.statusCode shouldBe 403
                    completeChallengeResult.body shouldBe "Cannot transition from create to complete"
                }
            }
        }

        "Calling the Redeem Challenge API" should {
            "should return the list of reward distribution transactions when passed valid parameters" {
                transaction {
                    map.put("path", "/challenge/redeem")
                    map.put("userId", user1.idValue.toString())
                    map.put("secretKey", newUsers[0].secretKey)
                    map.put("completerPublicKey", user2.cryptoKeyPair.publicKey)
                    map.put("challengeId", challenge.idValue)

                    val redeemChallengeResult = handler.handleRequest(map, contxt)
                    redeemChallengeResult.statusCode shouldBe 200

                    val transactionNamespaceList = JsonHelper.parse<TransactionNamespaceList>(redeemChallengeResult.body!!)
                    transactionNamespaceList.transactions.size shouldBe 2
                }

            }
            "should return a 403 forbidden response when attempted by a public key other than the sponsor's" {
                transaction {
                    map.put("path", "/challenge/complete")
                    map.put("userId", user2.idValue.toString())
                    map.put("secretKey", newUsers[0].secretKey)
                    map.put("completerPublicKey", user2.cryptoKeyPair.publicKey)
                    map.put("challengeId", challenge.idValue)

                    val redeemChallengeResult = handler.handleRequest(map, contxt)
                    redeemChallengeResult.statusCode shouldBe 403
                    redeemChallengeResult.body shouldBe "This user cannot change the challenge state"
                }
            }
            "should return a 403 forbidden response when challenge state cannot change to complete" {
                transaction {
                    map.put("path", "/challenge/redeem")
                    map.put("userId", user1.idValue.toString())
                    map.put("secretKey", newUsers[0].secretKey)
                    map.put("completerPublicKey", user2.cryptoKeyPair.publicKey)
                    map.put("challengeId", notActivatedChallenge.idValue)

                    val redeemChallengeResult = handler.handleRequest(map, contxt)
                    redeemChallengeResult.body shouldBe "Challenge has not been activated"
                }
            }
        }
    }
}