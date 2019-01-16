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
    private lateinit var newUserAccounts: List<NewUserAccount>
    private lateinit var challenge1: Challenge
    private lateinit var challenge2: Challenge

    override fun beforeTest(description: Description) {
        Handler.connectAndBuildTables()
        transaction {
            newUserAccounts = TestHelper.generateUserAccounts(2)
            challenge1 = TestHelper.generateChallenge(newUserAccounts[0].value,1)[0]
            challenge2 = TestHelper.generateChallenge(newUserAccounts[0].value,1)[0]
        }
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        "calling execute with a valid challenge" should {
            "return the users unshared transactions" {
                transaction {
                    var result = GetUnsharedTransactionsService.execute(
                        newUserAccounts[0].value,
                        challenge1.idValue
                    )
                    result.result shouldBe SOAResultType.SUCCESS
                    result.data!!.transactionsToShares.count() shouldBe 1
                    result.data!!.transactionsToShares.first().second shouldBe 100
                    TestHelper.generateShareTransaction(
                        challenge1,
                        newUserAccounts[0].value,
                        newUserAccounts[1].value,
                        result.data!!.transactionsToShares.first().first,
                        60
                    )
                    result = GetUnsharedTransactionsService.execute(
                        newUserAccounts[0].value,
                        challenge1.idValue
                    )
                    result.result shouldBe SOAResultType.SUCCESS
                    result.data!!.transactionsToShares.count() shouldBe 1
                    result.data!!.transactionsToShares.first().second shouldBe 40
                }
            }
        }

        "calling execute without specifying a challenge id" should {
            "return the users unshared transactions for all challenges" {
                transaction {
                    var result = GetUnsharedTransactionsService.execute(
                            newUserAccounts[0].value
                    )
                    result.result shouldBe SOAResultType.SUCCESS
                    result.data!!.transactionsToShares.count() shouldBe 2
                    result.data!!.transactionsToShares[0].second shouldBe 100
                    result.data!!.transactionsToShares[1].second shouldBe 100
                    TestHelper.generateShareTransaction(
                            challenge1,
                            newUserAccounts[0].value,
                            newUserAccounts[1].value,
                            result.data!!.transactionsToShares[0].first,
                            60
                    )
                    TestHelper.generateShareTransaction(
                            challenge1,
                            newUserAccounts[0].value,
                            newUserAccounts[1].value,
                            result.data!!.transactionsToShares[1].first,
                            50
                    )

                    // Test that shares were sent successfully and balances are reflected.
                    result = GetUnsharedTransactionsService.execute(
                            newUserAccounts[0].value
                    )
                    result.result shouldBe SOAResultType.SUCCESS
                    result.data!!.transactionsToShares.count() shouldBe 2
                    result.data!!.transactionsToShares[0].second shouldBe 40
                    result.data!!.transactionsToShares[1].second shouldBe 50
                }
            }
        }
    }
}