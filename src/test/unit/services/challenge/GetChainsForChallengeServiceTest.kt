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
    private lateinit var challengerGraph: Challenger<UserAccount>

    override fun beforeTest(description: Description) {
        Handler.connectAndBuildTables()
        transaction {
            newUserAccounts = TestHelper.generateUserAccounts(8)
            challenge1 = TestHelper.generateChallenge(newUserAccounts[0].value,1, true)[0]
            challengerGraph = TestHelper.createChainsOfShares(newUserAccounts, challenge1)
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

                    chainsResult.result shouldBe SOAResultType.SUCCESS
                    chainsResult.data!!.challenger.id shouldBe newUserAccounts[0].value.id
                    chainsResult.data!!.receivers!!.forEachIndexed { index, challenger ->
                        challenger.challenger.id shouldBe newUserAccounts[index + 1].value.id
                    }
                    chainsResult.data!!.receivers!![0].receivers!![0].challenger.id shouldBe newUserAccounts[5].value.id
                    chainsResult.data!!.receivers!![0].receivers!![1].challenger.id shouldBe newUserAccounts[6].value.id

                    chainsResult.data!!.receivers!![3].receivers!![0].challenger.id shouldBe newUserAccounts[7].value.id
                }
            }
        }
    }
}