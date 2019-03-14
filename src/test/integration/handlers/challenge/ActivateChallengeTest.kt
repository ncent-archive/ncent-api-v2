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
import test.TestHelper
import org.jetbrains.exposed.sql.transactions.transaction

@ExtendWith(MockKExtension::class)
class ActivateChallengeTest : WordSpec() {
    private lateinit var handler: Handler
    private lateinit var contxt: Context
    private lateinit var newUsers: List<NewUserAccount>
    private lateinit var user1: NewUserAccount
    private lateinit var user2: NewUserAccount
    private lateinit var challenge: Challenge
    private lateinit var map: Map<String, Any>

    override fun beforeTest(description: Description) {
        Handler.connectAndBuildTables()
        handler = Handler(true)
        contxt = mockk()
        transaction {
            newUsers = TestHelper.generateUserAccounts(2)
            user1 = newUsers[0]
            user2 = newUsers[1]
            challenge = TestHelper.generateFullChallenge(user1.value, user1.value, 1, true).first()
        }
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        "Calling the Activate Challenge API" should {
            "should return the transaction from the successful activation" {
                transaction {
                    map = TestHelper.buildRequest(
                            user1,
                            "/challenge/activate",
                            "PUT",
                            mapOf(
                                    Pair("challengeId", challenge.idValue)
                            )
                    )

                    val activateChallengeResult = handler.handleRequest(map, contxt)
                    activateChallengeResult.statusCode shouldBe 200

                    val transactionNamespace = JsonHelper.parse<TransactionNamespace>(activateChallengeResult.body!! as String)
                    transactionNamespace.to shouldBe challenge.cryptoKeyPair.publicKey
                }

            }
        }
    }
}