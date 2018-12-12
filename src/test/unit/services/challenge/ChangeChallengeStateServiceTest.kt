package test.unit.services.challenge

import framework.models.idValue
import io.kotlintest.*
import io.kotlintest.specs.WordSpec
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import main.daos.*
import kotlinserverless.framework.models.Handler
import kotlinserverless.framework.services.SOAResultType
import main.services.challenge.AddSubChallengeService
import main.services.challenge.ChangeChallengeStateService
import org.jetbrains.exposed.sql.transactions.transaction
import test.TestHelper

@ExtendWith(MockKExtension::class)
class ChangeChallengeStateServiceTest : WordSpec() {
    private lateinit var challenge1: Challenge
    private lateinit var challenge2: Challenge
    private lateinit var userAccount: UserAccount

    override fun beforeTest(description: Description) {
        Handler.connectAndBuildTables()
        transaction {
            userAccount = TestHelper.generateUserAccounts(1)[0]
            val challenges = TestHelper.generateFullChallenge(userAccount, userAccount,2)
            challenge1 = challenges[0]
            challenge2 = challenges[1]
        }
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        "calling execute with a valid state change" should {
            "allow for the state to be change" {
                transaction {
                    // start state is created
                    // try to change to created and fail
                    var result = ChangeChallengeStateService.execute(
                        userAccount.idValue,
                        mapOf(
                            Pair("challengeId", challenge1.idValue.toString()),
                            Pair("state", "CREATE")
                        )
                    )
                    result.message shouldBe "Cannot transition from create to create"
                    result.result shouldBe SOAResultType.FAILURE
                    // change to active successfully
                    result = ChangeChallengeStateService.execute(
                        userAccount.idValue,
                        mapOf(
                            Pair("challengeId", challenge1.idValue.toString()),
                            Pair("state", "ACTIVATE")
                        )
                    )
                    result.result shouldBe SOAResultType.SUCCESS
                    // change to invalid successfully
                    result = ChangeChallengeStateService.execute(
                        userAccount.idValue,
                        mapOf(
                            Pair("challengeId", challenge1.idValue.toString()),
                            Pair("state", "INVALIDATE")
                        )
                    )
                    result.result shouldBe SOAResultType.SUCCESS
                    // change to active successfully
                    result = ChangeChallengeStateService.execute(
                        userAccount.idValue,
                        mapOf(
                            Pair("challengeId", challenge1.idValue.toString()),
                            Pair("state", "ACTIVATE")
                        )
                    )
                    result.result shouldBe SOAResultType.SUCCESS
                    // change to complete successfully
                    result = ChangeChallengeStateService.execute(
                        userAccount.idValue,
                        mapOf(
                            Pair("challengeId", challenge1.idValue.toString()),
                            Pair("state", "COMPLETE")
                        )
                    )
                    result.result shouldBe SOAResultType.SUCCESS

                    // start state is created
                    // change to active successfully
                    result = ChangeChallengeStateService.execute(
                        userAccount.idValue,
                        mapOf(
                            Pair("challengeId", challenge2.idValue.toString()),
                            Pair("state", "ACTIVATE")
                        )
                    )
                    result.result shouldBe SOAResultType.SUCCESS
                    // change to expired successfully
                    result = ChangeChallengeStateService.execute(
                        userAccount.idValue,
                        mapOf(
                            Pair("challengeId", challenge2.idValue.toString()),
                            Pair("state", "EXPIRE")
                        )
                    )
                    result.result shouldBe SOAResultType.SUCCESS
                    // change to completed fails
                    result = ChangeChallengeStateService.execute(
                        userAccount.idValue,
                        mapOf(
                            Pair("challengeId", challenge2.idValue.toString()),
                            Pair("state", "COMPLETE")
                        )
                    )
                    result.message shouldBe "Cannot transition from expire to complete"
                    result.result shouldBe SOAResultType.FAILURE
                }
            }
        }
    }
}