package test.unit.services.challenge

import framework.models.idValue
import io.kotlintest.*
import io.kotlintest.specs.WordSpec
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import kotlinserverless.framework.models.Handler
import main.services.challenge.GenerateChallengeService
import org.jetbrains.exposed.sql.transactions.transaction
import test.TestHelper

@ExtendWith(MockKExtension::class)
class GenerateChallengeServiceTest : WordSpec() {
    private lateinit var challengeNamespace: ChallengeNamespace

    override fun beforeTest(description: Description) {
        Handler.connectAndBuildTables()
        transaction {
            val userAccount = TestHelper.generateUserAccounts().first()
            val challenges = TestHelper.generateChallenge(userAccount, 7)
            val parentChallenge = challenges[0]
            val syncSubChallenge1 = challenges[1]
            val syncSubChallenge2 = challenges[2]
            val syncSubChallenge3 = challenges[3]
            val asyncSubChallenge1 = challenges[4]
            val asyncSubChallenge2 = challenges[5]
            val asyncSubChallenge3 = challenges[6]
            val challengeSettingNamespace = TestHelper.generateChallengeSettingsNamespace(userAccount).first()
            val resultVectorNamespace = TestHelper.generateResultVectorNamespace(userAccount, 2)
            val resultVector1 = resultVectorNamespace[0]
            val resultVector2 = resultVectorNamespace[1]
            challengeNamespace = ChallengeNamespace(
                parentChallenge = parentChallenge.idValue,
                challengeSettings = challengeSettingNamespace,
                asyncSubChallenges = listOf(asyncSubChallenge1, asyncSubChallenge2, asyncSubChallenge3).map { it.idValue },
                syncSubChallenges = listOf(syncSubChallenge1, syncSubChallenge2, syncSubChallenge3).map { it.idValue },
                resultVectors = listOf(resultVector1, resultVector2)
            )
        }
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        "calling execute with a valid completion criteria" should {
            "generate the reward and associated reward type and pool and completion criteria" {
                var result = GenerateChallengeService.execute(null, challengeNamespace, null)
                result.result shouldBe SOAResultType.SUCCESS
                transaction {
                    val challenge = result.data!!
                }
            }
        }
    }
}