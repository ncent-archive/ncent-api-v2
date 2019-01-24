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
class FindAllBalancesForChallengeTest : WordSpec() {
    private lateinit var handler: Handler
    private lateinit var contxt: Context
    private lateinit var newUsers: List<NewUserAccount>
    private lateinit var user1: NewUserAccount
    private lateinit var user2: NewUserAccount
    private lateinit var challenge: Challenge
    private lateinit var notActivatedChallenge: Challenge
    private lateinit var map: Map<String, Any>

    override fun beforeTest(description: Description) {
        Handler.connectAndBuildTables()
        handler = Handler()
        contxt = mockk()
        transaction {
            newUsers = TestHelper.generateUserAccounts(2)
            user1 = newUsers[0]
            user2 = newUsers[1]
            challenge = TestHelper.generateFullChallenge(user1.value, user1.value, 1, true).first()
            notActivatedChallenge = TestHelper.generateFullChallenge(user1.value, user1.value, 1, true).first()
            ActivateChallengeService.execute(user1.value, challenge.idValue)
            ShareChallengeService.execute(user1.value, challenge, 2, user2.value.cryptoKeyPair.publicKey, null, null)
            ShareChallengeService.execute(user1.value, notActivatedChallenge, 2, user2.value.cryptoKeyPair.publicKey, null, null)
        }
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        "Calling the API with the sponsor" should {
            "should return a valid list of users with their balances" {
                transaction {
                    map = TestHelper.buildRequest(
                            user1,
                            "/challenge/balances",
                            "GET",
                            mapOf(
                                    Pair("userId", user1.value.idValue.toString()),
                                    Pair("challengeId", challenge.idValue)
                            )
                    )

                    val getAllBalancesForChallengeResult = handler.handleRequest(map, contxt)
                    getAllBalancesForChallengeResult.statusCode shouldBe 200

                    val emailToChallengeBalanceList = JsonHelper.parse<EmailToChallengeBalanceList>(getAllBalancesForChallengeResult.body!!)
                    var totalBalance = 0
                    for (key in emailToChallengeBalanceList.emailToChallengeBalances.keys) {
                        totalBalance += emailToChallengeBalanceList.emailToChallengeBalances[key]!!
                    }

                    totalBalance shouldBe 100
                }
            }
        }

        "Calling the API with a non-sponsor" should {
            "should return a 403 forbidden response" {
                transaction {
                    map = TestHelper.buildRequest(
                            user2,
                            "/challenge/balances",
                            "GET",
                            mapOf(
                                    Pair("userId", user2.value.idValue.toString()),
                                    Pair("challengeId", challenge.idValue)
                            )
                    )

                    val getAllBalancesForChallengeResult = handler.handleRequest(map, contxt)
                    getAllBalancesForChallengeResult.statusCode shouldBe 403
                    getAllBalancesForChallengeResult.body shouldBe "User not permitted to make this call"
                }
            }
        }
    }
}