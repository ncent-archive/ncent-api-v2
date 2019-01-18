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
    private lateinit var parentChallenge: Challenge
    private lateinit var userAccount: UserAccount

    override fun beforeTest(description: Description) {
        Handler.connectAndBuildTables()
        transaction {
            val newUsers = TestHelper.generateUserAccounts()
            userAccount = newUsers[0].value
            val challenges = TestHelper.generateChallenge(userAccount, 7)
            parentChallenge = challenges[0]
            val syncSubChallenge1 = challenges[1]
            val syncSubChallenge2 = challenges[2]
            val syncSubChallenge3 = challenges[3]
            val asyncSubChallenge1 = challenges[4]
            val asyncSubChallenge2 = challenges[5]
            val asyncSubChallenge3 = challenges[6]
            val distributionFeeRewardNamespace = TestHelper.generateRewardNamespace(RewardTypeName.SINGLE)
            val challengeSettingNamespace = TestHelper.generateChallengeSettingsNamespace(userAccount).first()
            val completionCriteriasNamespace = TestHelper.generateCompletionCriteriaNamespace(userAccount, 2)
            val completionCriteria1 = completionCriteriasNamespace[0]
            challengeNamespace = ChallengeNamespace(
                parentChallenge = parentChallenge.idValue,
                challengeSettings = challengeSettingNamespace,
                subChallenges = listOf(
                        SubChallengeNamespace(asyncSubChallenge1.idValue, SubChallengeType.ASYNC.toString()),
                        SubChallengeNamespace(asyncSubChallenge2.idValue, SubChallengeType.ASYNC.toString()),
                        SubChallengeNamespace(asyncSubChallenge3.idValue, SubChallengeType.ASYNC.toString()),
                        SubChallengeNamespace(syncSubChallenge1.idValue, SubChallengeType.SYNC.toString()),
                        SubChallengeNamespace(syncSubChallenge2.idValue, SubChallengeType.SYNC.toString()),
                        SubChallengeNamespace(syncSubChallenge3.idValue, SubChallengeType.SYNC.toString())
                ),
                completionCriteria = completionCriteria1,
                distributionFeeReward = distributionFeeRewardNamespace
            )
        }
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        "calling execute with a valid completion criteria" should {
            "generate the reward and associated reward type and pool and completion criteria" {
                transaction {
                    var result = GenerateChallengeService.execute(userAccount, challengeNamespace)
                    result.result shouldBe SOAResultType.SUCCESS
                    val challenge = result.data!!
                    challenge.cryptoKeyPair shouldNotBe null
                    challenge.challengeSettings.name shouldBe "TESTname0"
                    challenge.subChallenges.count() shouldBe 6
                    challenge.subChallenges.filter { it.type == SubChallengeType.ASYNC }.count() shouldBe 3
                    challenge.subChallenges.filter { it.type == SubChallengeType.SYNC }.count() shouldBe 3
                    challenge.parentChallenge!!.idValue shouldBe parentChallenge.idValue
                    challenge.completionCriterias.address shouldBe userAccount.cryptoKeyPair.publicKey
                    challenge.completionCriterias.reward.pool shouldNotBe null
                }
            }
        }
    }
}