package test.unit.services.challenge

import framework.models.idValue
import io.kotlintest.Description
import io.kotlintest.TestResult
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import io.mockk.junit5.MockKExtension
import kotlinserverless.framework.models.Handler
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import main.services.challenge.GetOneChallengeService
import test.TestHelper
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class GetOneChallengeServiceTest: WordSpec() {
    private lateinit var user: UserAccount

    override fun beforeTest(description: Description) {
        Handler.connectAndBuildTables()
        user = TestHelper.generateUserAccounts().first().value
        TestHelper.generateFullChallenge(user, user).first()
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        "calling execute with a valid challenge Id" should {
            transaction {
                val challenge = Challenge.all().first()
                val getOneChallengeResult = GetOneChallengeService.execute(challenge.idValue)
                getOneChallengeResult.result shouldBe SOAResultType.SUCCESS
                getOneChallengeResult.data?.idValue shouldBe challenge.idValue
            }
        }

        "calling execute with an invalid challengeId" should {
            val badChallengeId = 324
            val getOneChallengeResult = GetOneChallengeService.execute(badChallengeId)
            getOneChallengeResult.result shouldBe SOAResultType.FAILURE
        }
    }
}