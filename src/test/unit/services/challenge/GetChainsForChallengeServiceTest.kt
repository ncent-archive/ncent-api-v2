package test.unit.services.challenge

import framework.models.idValue
import io.kotlintest.*
import io.kotlintest.specs.WordSpec
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import kotlinserverless.framework.models.Handler
import main.services.challenge.GetChainsForChallengeService
import main.services.challenge.GetUnsharedTransactionsService
import main.services.challenge.ShareChallengeService
import org.jetbrains.exposed.sql.transactions.transaction
import test.TestHelper

@ExtendWith(MockKExtension::class)
class GetChainsForChallengeServiceTest : WordSpec() {
    private lateinit var newUserAccounts: List<NewUserAccount>
    private lateinit var challenge1: Challenge

    override fun beforeTest(description: Description) {
        Handler.connectAndBuildTables()
        transaction {
            newUserAccounts = TestHelper.generateUserAccounts(8)
            challenge1 = TestHelper.generateChallenge(newUserAccounts[0].value,1, true)[0]
            createChainsOfShares()
        }
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        "calling execute with a valid challenge" should {
            "return the chains of emails" {
                transaction {
                    val chainsResult = GetChainsForChallengeService.execute(
                        newUserAccounts[0].value,
                        challenge1.idValue
                    )

                    chainsResult.result shouldBe  SOAResultType.SUCCESS
                    chainsResult.data!!.count() shouldBe 5
                    chainsResult.data!!.first().count() shouldBe 3
                    chainsResult.data!! shouldBe mutableListOf(
                        mutableListOf(
                            "dev0@ncnt.io", "dev1@ncnt.io", "dev5@ncnt.io"
                        ),
                        mutableListOf(
                            "dev0@ncnt.io", "dev2@ncnt.io"
                        ),
                        mutableListOf(
                            "dev0@ncnt.io", "dev3@ncnt.io"
                        ),
                        mutableListOf(
                            "dev0@ncnt.io", "dev4@ncnt.io", "dev7@ncnt.io"
                        ),
                        mutableListOf(
                            "dev0@ncnt.io", "dev1@ncnt.io", "dev6@ncnt.io"
                        )
                    )
                }
            }
        }
    }

    /**
     *          0
     *        / \ \  \
     *       1  2  3  4
     *      / \       \
     *     5  6        7
     */
    private fun createChainsOfShares() {
        ShareChallengeService.execute(
            newUserAccounts[0].value,
            challenge1,
            1,
            newUserAccounts[1].value.cryptoKeyPair.publicKey
        )

        ShareChallengeService.execute(
            newUserAccounts[0].value,
            challenge1,
            1,
            newUserAccounts[2].value.cryptoKeyPair.publicKey
        )

        ShareChallengeService.execute(
            newUserAccounts[0].value,
            challenge1,
            1,
            newUserAccounts[3].value.cryptoKeyPair.publicKey
        )

        ShareChallengeService.execute(
            newUserAccounts[0].value,
            challenge1,
            1,
            newUserAccounts[4].value.cryptoKeyPair.publicKey
        )

        ShareChallengeService.execute(
            newUserAccounts[1].value,
            challenge1,
            1,
            newUserAccounts[5].value.cryptoKeyPair.publicKey
        )

        ShareChallengeService.execute(
            newUserAccounts[1].value,
            challenge1,
            1,
            newUserAccounts[6].value.cryptoKeyPair.publicKey
        )

        ShareChallengeService.execute(
            newUserAccounts[4].value,
            challenge1,
            1,
            newUserAccounts[7].value.cryptoKeyPair.publicKey
        )
    }
}