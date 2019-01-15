package test.unit.services.challenge

import framework.models.idValue
import io.kotlintest.*
import io.kotlintest.specs.WordSpec
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import main.daos.*
import kotlinserverless.framework.models.Handler
import main.services.challenge.AddSubChallengeService
import org.jetbrains.exposed.sql.transactions.transaction
import test.TestHelper

@ExtendWith(MockKExtension::class)
class AddSubChallengeServiceTest : WordSpec() {
    private lateinit var challenge: Challenge
    private lateinit var userAccount: UserAccount

    override fun beforeTest(description: Description) {
        Handler.connectAndBuildTables()
        transaction {
            val newUsers = TestHelper.generateUserAccounts()
            userAccount = newUsers[0].value
            challenge = TestHelper.generateFullChallenge(userAccount, userAccount,1)[0]
        }
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        "calling execute with a valid sub challenge and challenge" should {
            "generate the sub challenge and add it to the challenge" {
                transaction {
                    challenge.subChallenges.count() shouldBe 2
                    val subChallengeNamespace = TestHelper.generateChallengeNamespace(userAccount, 1)[0]
                    AddSubChallengeService.execute(
                        userAccount,
                        subChallengeNamespace,
                        challenge.idValue,
                        SubChallengeType.ASYNC
                    )

                    val updatedChallenge = Challenge.findById(challenge.id)!!
                    updatedChallenge.subChallenges.count() shouldBe 3
                }
            }
        }
    }
}