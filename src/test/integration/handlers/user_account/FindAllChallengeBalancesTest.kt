package test.integration.handlers.user_account

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
import test.TestHelper
import org.jetbrains.exposed.sql.transactions.transaction

@ExtendWith(MockKExtension::class)
class FindAllChallengeBalancesTest : WordSpec() {
    private lateinit var handler: Handler
    private lateinit var contxt: Context
    private lateinit var user1: NewUserAccount
    private lateinit var user2: NewUserAccount
    private lateinit var challenge: Challenge
    private lateinit var map: Map<String, Any>
    private lateinit var notFoundMap: Map<String, Any>

    override fun beforeTest(description: Description) {
        Handler.connectAndBuildTables()
        handler = Handler()
        contxt = mockk()
        transaction {
            val newUsers = TestHelper.generateUserAccounts(2)
            user1 = newUsers[0]
            user2 = newUsers[1]
            challenge = TestHelper.generateFullChallenge(user1.value, user1.value).first()
            map = TestHelper.buildRequest(
                user1,
                "/user/balances",
                "GET",
                mapOf(
                    Pair("userId", user1.value.idValue.toString())
                )
            )
            notFoundMap = TestHelper.buildRequest(
                user2,
                "/user/balances",
                "GET",
                mapOf(
                    Pair("userId", user2.value.idValue.toString())
                )
            )
        }
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        "Calling the API with a user that has challenges" should {
            "should return a valid ChallengesToUnsharedTransactions object" {
                transaction {
                    val findAllChallengesResult = handler.handleRequest(map, contxt)
                    findAllChallengesResult.statusCode shouldBe 200

                    val challengeToUnsharedTransactionNamespaceList = JsonHelper.parse<ChallengeToUnsharedTransactionsNamespaceList>(findAllChallengesResult.body!!)
                    challengeToUnsharedTransactionNamespaceList.challengeToUnsharedTransactions.size shouldBe 4
                }
            }
        }

        "Calling the API with a user that has no challenges" should {
            "should return a 404 not found response" {
                transaction {
                    val findAllChallengesResult = handler.handleRequest(notFoundMap, contxt)
                    findAllChallengesResult.statusCode shouldBe 404
                    findAllChallengesResult.body shouldBe "No challenges found for ${user2.value.cryptoKeyPair.publicKey}"
                }
            }
        }
    }
}