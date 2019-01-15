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
    private lateinit var challenge: Challenge

    override fun beforeTest(description: Description) {
        Handler.connectAndBuildTables()
        transaction {
            newUserAccounts = TestHelper.generateUserAccounts(2)
            challenge = TestHelper.generateFullChallenge(newUserAccounts[0].value, newUserAccounts[0].value,1)[0]
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
                        challenge.idValue
                    )
                    result.result shouldBe SOAResultType.SUCCESS
                    result.data!!.transactionsToShares.count() shouldBe 1
                    result.data!!.transactionsToShares.first().second shouldBe 100
                    TestHelper.generateShareTransaction(
                        challenge,
                        newUserAccounts[0].value,
                        newUserAccounts[1].value,
                        result.data!!.transactionsToShares.first().first,
                        60
                    )
                    result = GetUnsharedTransactionsService.execute(
                        newUserAccounts[0].value,
                        challenge.idValue
                    )
                    result.result shouldBe SOAResultType.SUCCESS
                    result.data!!.transactionsToShares.count() shouldBe 1
                    result.data!!.transactionsToShares.first().second shouldBe 40
                }
            }
        }
    }
}