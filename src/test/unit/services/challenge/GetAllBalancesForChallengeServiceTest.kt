package test.unit.services.challenge

import framework.models.idValue
import io.kotlintest.*
import io.kotlintest.specs.WordSpec
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import kotlinserverless.framework.models.Handler
import main.services.challenge.ActivateChallengeService
import main.services.challenge.GetAllBalancesForChallengeService
import main.services.challenge.ShareChallengeService
import org.jetbrains.exposed.sql.transactions.transaction
import test.TestHelper

@ExtendWith(MockKExtension::class)
class GetAllBalancesForChallengeServiceTest : WordSpec() {
    private lateinit var newUsers: List<NewUserAccount>
    private lateinit var user1: UserAccount
    private lateinit var user2: UserAccount
    private lateinit var challenge: Challenge

    override fun beforeTest(description: Description) {
        Handler.connectAndBuildTables()
        transaction {
            newUsers = TestHelper.generateUserAccounts(2)
            user1 = newUsers[0].value
            user2 = newUsers[1].value
            challenge = TestHelper.generateFullChallenge(user1, user1, 1, true).first()
            ActivateChallengeService.execute(user1, challenge.idValue)
            ShareChallengeService.execute(user1, challenge, 2, user2.cryptoKeyPair.publicKey, null, null)
        }
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        "calling execute with the challenge sponsor" should {
            "return all user emails and their balances" {
                transaction {
                    val getAllBalancesForChallengeResult = GetAllBalancesForChallengeService.execute(user1, challenge.idValue)
                    getAllBalancesForChallengeResult.result shouldBe SOAResultType.SUCCESS

                    val keyToBalances = getAllBalancesForChallengeResult.data!!.emailToChallengeBalances
                    var totalBalance = 0

                    for (key in keyToBalances.keys) {
                        totalBalance += keyToBalances[key]!!
                    }

                    totalBalance shouldBe 100
                }
            }
        }

        "calling execute without the challenge sponsor" should {
            "return an SOA failure" {
                transaction {
                    val getAllBalancesForChallengeResult = GetAllBalancesForChallengeService.execute(user2, challenge.idValue)
                    getAllBalancesForChallengeResult.result shouldBe SOAResultType.FAILURE
                    getAllBalancesForChallengeResult.message shouldBe "User not permitted to make this call"
                }
            }
        }
    }

}