package test.unit.services.challenge

import framework.models.idValue
import io.kotlintest.*
import io.kotlintest.specs.WordSpec
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import main.daos.*
import kotlinserverless.framework.models.Handler
import kotlinserverless.framework.services.SOAResultType
import main.services.challenge.ActivateChallengeService
import main.services.challenge.CompleteChallengeService
import main.services.challenge.RedeemChallengeService
import main.services.challenge.ShareChallengeService
import org.jetbrains.exposed.sql.transactions.transaction
import test.TestHelper

@ExtendWith(MockKExtension::class)
class RedeemChallengeServiceTest : WordSpec() {
    private lateinit var challenge: Challenge
    private lateinit var userAccount1: UserAccount
    private lateinit var userAccount2: UserAccount
    private lateinit var userAccount3: UserAccount

    override fun beforeTest(description: Description) {
        Handler.connectAndBuildTables()
        transaction {
            val newUserAccounts = TestHelper.generateUserAccounts(3)
            userAccount1 = newUserAccounts[0].value
            userAccount2 = newUserAccounts[1].value
            userAccount3 = newUserAccounts[2].value
            /**
             *          user1 (100)
             */
            challenge = TestHelper.generateFullChallenge(userAccount1, userAccount1,1, true)[0]
        }
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        // TODO test off chain
        // TODO test if multi-tx share fails midway
        "calling execute with valid data" should {
            "should distribute rewards" {
                transaction {
                    ActivateChallengeService.execute(userAccount1, challenge.idValue)

                    /**
                     *          user1 (50)
                     *            |
                     *          user2 (50)
                     */
                    ShareChallengeService.execute(
                        userAccount1,
                        challenge,
                        50,
                        userAccount2.cryptoKeyPair.publicKey
                    )
                    /**
                     *          user1 (20)
                     *            |
                     *          user2 (50, 30)
                     */
                    ShareChallengeService.execute(
                        userAccount1,
                        challenge,
                        30,
                        userAccount2.cryptoKeyPair.publicKey
                    )
                    /**
                     *          user1 (20)
                     *            |
                     *          user2 (0)
                     *            |
                     *          user3 (80)
                     */
                    ShareChallengeService.execute(
                        userAccount2,
                        challenge,
                        80,
                        userAccount3.cryptoKeyPair.publicKey
                    )

                    // user 2 should not be able to complete the challenge

                    var result = RedeemChallengeService.execute(
                        userAccount1,
                        challenge,
                        userAccount2.cryptoKeyPair.publicKey
                    )
                    result.result shouldBe SOAResultType.FAILURE
                    result.message shouldBe "User must have a share in order to complete"

                    // user 3 should be able to complete the challenge
                    // should generate 3 transactions paying out user 3,2,1
                    var redeemResult = RedeemChallengeService.execute(
                        userAccount1,
                        challenge,
                        userAccount3.cryptoKeyPair.publicKey
                    )
                    redeemResult.result shouldBe SOAResultType.SUCCESS
                    val distributionResults = redeemResult.data!!.transactions
                    distributionResults.count() shouldBe 3
                }
            }
        }
    }
}