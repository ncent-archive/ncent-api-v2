package test.unit.services.challenge

import framework.models.idValue
import io.kotlintest.*
import io.kotlintest.specs.WordSpec
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import main.daos.*
import kotlinserverless.framework.models.Handler
import kotlinserverless.framework.services.SOAResultType
import main.services.challenge.ShareChallengeService
import org.jetbrains.exposed.sql.transactions.transaction
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
            val userAccounts = TestHelper.generateUserAccounts(3)
            userAccount1 = userAccounts[0]
            userAccount2 = userAccounts[1]
            userAccount3 = userAccounts[2]
            challenge = TestHelper.generateFullChallenge(userAccount1, userAccount1,1)[0]
        }
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        "calling execute with enough shares available in one tx" should {
            "generate a single transaction sharing to the user" {
                transaction {
                    val result = ShareChallengeService.execute(userAccount1.idValue, mapOf(
                        Pair("publicKeyToShareWith", userAccount2.cryptoKeyPair.publicKey),
                        Pair("challengeId", challenge.idValue.toString()),
                        Pair("shares", 50.toString())
                    ))
                    result.result shouldBe SOAResultType.SUCCESS
                    result.data!!.transactions.count() shouldBe 1
                }
            }
        }
        // TODO test if multi-tx share fails midway
        "calling execute with enough shares available in multiple tx" should {
            "generate a multiple transaction sharing to the user" {
                transaction {
                    ShareChallengeService.execute(userAccount1.idValue, mapOf(
                        Pair("publicKeyToShareWith", userAccount2.cryptoKeyPair.publicKey),
                        Pair("challengeId", challenge.idValue.toString()),
                        Pair("shares", 50.toString())
                    ))
                    ShareChallengeService.execute(userAccount1.idValue, mapOf(
                        Pair("publicKeyToShareWith", userAccount2.cryptoKeyPair.publicKey),
                        Pair("challengeId", challenge.idValue.toString()),
                        Pair("shares", 30.toString())
                    ))
                    var result = ShareChallengeService.execute(userAccount2.idValue, mapOf(
                        Pair("publicKeyToShareWith", userAccount3.cryptoKeyPair.publicKey),
                        Pair("challengeId", challenge.idValue.toString()),
                        Pair("shares", 90.toString())
                    ))
                    result.result shouldBe SOAResultType.FAILURE
                    result = ShareChallengeService.execute(userAccount2.idValue, mapOf(
                        Pair("publicKeyToShareWith", userAccount3.cryptoKeyPair.publicKey),
                        Pair("challengeId", challenge.idValue.toString()),
                        Pair("shares", 80.toString())
                    ))
                    result.result shouldBe SOAResultType.SUCCESS
                    result.data!!.transactions.count() shouldBe 2
                }
            }
        }
        "calling execute without enough shares available" should {
            "fails to generate any new transactions" {
                transaction {
                    val result = ShareChallengeService.execute(userAccount1.idValue, mapOf(
                        Pair("publicKeyToShareWith", userAccount2.cryptoKeyPair.publicKey),
                        Pair("challengeId", challenge.idValue.toString()),
                        Pair("shares", 2000.toString())
                    ))
                    result.result shouldBe SOAResultType.FAILURE
                }
            }
        }
    }
}