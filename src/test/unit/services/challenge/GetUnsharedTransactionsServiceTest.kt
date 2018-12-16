package test.unit.services.challenge

import framework.models.idValue
import io.kotlintest.*
import io.kotlintest.specs.WordSpec
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import kotlinserverless.framework.models.Handler
import main.services.challenge.GetUnsharedTransactionsService
import org.jetbrains.exposed.sql.transactions.transaction
import test.TestHelper

@ExtendWith(MockKExtension::class)
class GetUnsharedTransactionsServiceTest : WordSpec() {
    private lateinit var userAccounts: List<UserAccount>
    private lateinit var challenge: Challenge

    override fun beforeTest(description: Description) {
        Handler.connectAndBuildTables()
        transaction {
            userAccounts = TestHelper.generateUserAccounts(2)
            challenge = TestHelper.generateFullChallenge(userAccounts[0], userAccounts[0],1)[0]
        }
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        "calling execute with a valid challenge" should {
            "return the users unshared transactions" {
                var result = GetUnsharedTransactionsService.execute(
                    userAccounts[0].idValue,
                    mapOf(
                        Pair("challengeId", challenge.idValue.toString())
                    )
                )
                result.result shouldBe SOAResultType.SUCCESS
                transaction {
                    result.data!!.transactionsToShares.count() shouldBe 1
                    result.data!!.transactionsToShares.first().second shouldBe 100
                    TestHelper.generateShareTransaction(
                        challenge,
                        userAccounts[0],
                        userAccounts[1],
                        result.data!!.transactionsToShares.first().first,
                        60
                    )
                    result = GetUnsharedTransactionsService.execute(
                        userAccounts[0].idValue,
                        mapOf(
                            Pair("challengeId", challenge.idValue.toString())
                        )
                    )
                    result.result shouldBe SOAResultType.SUCCESS
                    result.data!!.transactionsToShares.count() shouldBe 1
                    result.data!!.transactionsToShares.first().second shouldBe 40
                }
            }
        }
    }
}