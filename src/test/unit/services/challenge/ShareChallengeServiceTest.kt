package test.unit.services.challenge

import framework.models.idValue
import io.kotlintest.*
import io.kotlintest.specs.WordSpec
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import main.daos.*
import kotlinserverless.framework.models.Handler
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.services.challenge.GetUnsharedTransactionsService
import main.services.challenge.ShareChallengeService
import main.services.challenge.ValidateShareService
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import test.TestHelper

@ExtendWith(MockKExtension::class)
class ShareChallengeServiceTest : WordSpec() {
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
            challenge = TestHelper.generateFullChallenge(userAccount1, userAccount1,1)[0]
        }
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        // TODO test off chain
        "calling execute with enough shares available in one tx" should {
            "generate a single transaction sharing to the user" {
                transaction {
                    val result = ShareChallengeService.execute(
                        userAccount1,
                        challenge,
                        userAccount2.cryptoKeyPair.publicKey,
                        50,
                        null
                    )
                    result.result shouldBe SOAResultType.SUCCESS
                    result.data!!.transactions.count() shouldBe 1

                    val result2 = GetUnsharedTransactionsService.execute(userAccount1, challenge.idValue)
                    result2.data!!.transactionsToShares.map { it.second }.sum() shouldBe 50
                }
            }
        }
        // TODO test if multi-tx share fails midway
        "calling execute with enough shares available in multiple tx" should {
            "generate a multiple transaction sharing to the user" {
                transaction {
                    ShareChallengeService.execute(
                        userAccount1,
                        challenge,
                        userAccount2.cryptoKeyPair.publicKey,
                        50,
                        null
                    )
                    ShareChallengeService.execute(
                        userAccount1,
                        challenge,
                        userAccount2.cryptoKeyPair.publicKey,
                        30,
                        null
                    )
                    val result2 = GetUnsharedTransactionsService.execute(userAccount1, challenge.idValue)
                    result2.data!!.transactionsToShares.map { it.second }.sum() shouldBe 20

                    var result = ShareChallengeService.execute(
                        userAccount2,
                        challenge,
                        userAccount3.cryptoKeyPair.publicKey,
                        90,
                        null
                    )
                    result.result shouldBe SOAResultType.FAILURE
                    result = ShareChallengeService.execute(
                        userAccount2,
                        challenge,
                        userAccount3.cryptoKeyPair.publicKey,
                        80,
                        null
                    )
                    result.result shouldBe SOAResultType.SUCCESS
                    result.data!!.transactions.count() shouldBe 2

                    val result3 = GetUnsharedTransactionsService.execute(userAccount3, challenge.idValue)
                    result3.data!!.transactionsToShares.map { it.second }.sum() shouldBe 80
                }
            }
        }
        "calling execute without enough shares available" should {
            "fails to generate any new transactions" {
                transaction {
                    val result = ShareChallengeService.execute(
                        userAccount1,
                        challenge,
                        userAccount2.cryptoKeyPair.publicKey,
                        2000,
                        null
                    )
                    result.result shouldBe SOAResultType.FAILURE
                }
            }
        }
        "calling execute with an expired share" should {
            "fails to generate any new transactions" {
                transaction {
                    val unsharedTransactions = ValidateShareService.execute(userAccount1, challenge, 1).data!!.second!!

                    // Set the expiration for shares to one day earlier.
                    unsharedTransactions.transactionsToShares.forEach {
                        it.first.metadatas.filter { it.key == "shareExpiration" }.first().value = DateTime.now().minusDays(1).toString()
                    }

                    val result = ShareChallengeService.execute(
                        userAccount1,
                        challenge,
                        userAccount2.cryptoKeyPair.publicKey,
                        1,
                        null
                    )
                    result.result shouldBe SOAResultType.FAILURE
                }
            }
        }
    }
}