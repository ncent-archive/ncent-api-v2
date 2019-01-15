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
import main.services.challenge.ChangeChallengeStateService
import org.jetbrains.exposed.sql.transactions.transaction
import test.TestHelper

@ExtendWith(MockKExtension::class)
class ActivateChallengeServiceTest : WordSpec() {
    private lateinit var challenge1: Challenge
    private lateinit var userAccount: UserAccount

    override fun beforeTest(description: Description) {
        Handler.connectAndBuildTables()
        transaction {
            val newUsers = TestHelper.generateUserAccounts()
            userAccount = newUsers[0].value
            challenge1 = TestHelper.generateFullChallenge(userAccount, userAccount,1)[0]
        }
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        "calling execute with a valid challenge" should {
            "allow for the state to be change" {
                transaction {
                    // start state is created
                    // change to active successfully
                    var result = ActivateChallengeService.execute(
                        userAccount,
                        challenge1.idValue
                    )
                    result.result shouldBe SOAResultType.SUCCESS
                    result.data!!.action.type shouldBe ActionType.ACTIVATE
                    // change to invalid successfully
                    ChangeChallengeStateService.execute(
                        userAccount,
                        challenge1.idValue,
                        ActionType.INVALIDATE
                    )
                    // change to active successfully
                    result = ActivateChallengeService.execute(
                        userAccount,
                        challenge1.idValue
                    )
                    result.result shouldBe SOAResultType.SUCCESS
                    result.data!!.action.type shouldBe ActionType.ACTIVATE
                }
            }
        }
    }
}