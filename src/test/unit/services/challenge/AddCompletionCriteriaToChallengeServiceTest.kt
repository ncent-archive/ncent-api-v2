package test.unit.services.challenge

import framework.models.idValue
import io.kotlintest.*
import io.kotlintest.specs.WordSpec
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import main.daos.*
import kotlinserverless.framework.models.Handler
import main.services.challenge.AddCompletionCriteriaToChallengeService
import org.jetbrains.exposed.sql.transactions.transaction
import test.TestHelper

@ExtendWith(MockKExtension::class)
class AddCompletionCriteriaToChallengeServiceTest : WordSpec() {
    private lateinit var challenge: Challenge
    private lateinit var userAccount: UserAccount

    override fun beforeTest(description: Description) {
        Handler.connectAndBuildTables()
        transaction {
            userAccount = TestHelper.generateUserAccounts(1)[0]
            challenge = TestHelper.generateFullChallenge(userAccount, userAccount,1)[0]
        }
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        "calling execute with a valid completion criteria and challenge" should {
            "generate the completion criteria and add it to the challenge" {
                transaction {
                    challenge.completionCriterias.count() shouldBe 2
                    val completionCriteriaNamespace = TestHelper.generateCompletionCriteriaNamespace(userAccount, 1)[0]
                    AddCompletionCriteriaToChallengeService.execute(
                        userAccount.idValue,
                        completionCriteriaNamespace,
                        mapOf(Pair("challengeId", challenge.idValue.toString()))
                    )

                    val updatedChallenge = Challenge.findById(challenge.id)!!
                    updatedChallenge.completionCriterias.count() shouldBe 3
                }
            }
        }
    }
}