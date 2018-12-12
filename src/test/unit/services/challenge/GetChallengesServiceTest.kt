package test.unit.services.challenge

import framework.models.idValue
import io.kotlintest.*
import io.kotlintest.specs.WordSpec
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import kotlinserverless.framework.models.Handler
import main.services.challenge.GetChallengesService
import org.jetbrains.exposed.sql.transactions.transaction
import test.TestHelper

@ExtendWith(MockKExtension::class)
class GetChallengesServiceTest : WordSpec() {
    private lateinit var userAccount: UserAccount

    override fun beforeTest(description: Description) {
        Handler.connectAndBuildTables()
        transaction {
            val userAccounts = TestHelper.generateUserAccounts(2)
            userAccount = userAccounts[0]
            TestHelper.generateFullChallenge(userAccount, userAccounts[1],2)
            TestHelper.generateFullChallenge(userAccounts[1], userAccounts[1],2)
        }
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        "calling execute with a valid user who has challenges" should {
            "return the challenges" {
                var result = GetChallengesService.execute(userAccount.idValue)
                result.result shouldBe SOAResultType.SUCCESS
                transaction {
                    val challenges = result.data!!.challenges
                    challenges.count() shouldBe 2
                    challenges.first().subChallenges.count() shouldBe 2
                    challenges.first().completionCriterias.count() shouldBe 2
                    // the challenges + sub challenges + parent challenges
                    // the generateFullChallenge generates 1 parent, 1 challenge, 2 sub challenges
                    // so 4 challenges per generateFullChallenge challenge
                    // thus calling generateFullChallenge with 2 -- generates 8
                    // only two of with are associated to the main userAccount
                    Challenge.all().count() shouldBe 16
                    // TODO should probably also share challenge with this user and test if it is part of the list
                }
            }
        }
    }
}