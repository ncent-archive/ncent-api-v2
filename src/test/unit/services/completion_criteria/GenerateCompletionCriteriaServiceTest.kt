package test.unit.services.completion_criteria

import io.kotlintest.*
import io.kotlintest.specs.WordSpec
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import kotlinserverless.framework.models.Handler
import main.services.completion_criteria.GenerateCompletionCriteriaService
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import test.TestHelper

@ExtendWith(MockKExtension::class)
class GenerateCompletionCriteriaServiceTest : WordSpec() {
    private lateinit var completionCriteriaNamespace: CompletionCriteriaNamespace
    private val address = "SOMEADDRESS"

    override fun beforeTest(description: Description): Unit {
        Handler.connectAndBuildTables()
        var rewardNamespace = RewardNamespace(
            type = RewardTypeNamespace(
                audience = Audience.FULL,
                type = RewardTypeName.EVEN
            ),
            metadatas = arrayOf(MetadatasNamespace("title", "reward everyone"))
        )
        completionCriteriaNamespace = CompletionCriteriaNamespace(
            address = address,
            reward =  rewardNamespace,
            prereq = listOf()
        )
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        "calling execute with a valid completion criteria" should {
            "generate the reward and associated reward type and pool and completion criteria" {
                transaction {
                    val newUserAccounts = TestHelper.generateUserAccounts()
                    var result = GenerateCompletionCriteriaService.execute(newUserAccounts[0].value, completionCriteriaNamespace)
                    result.result shouldBe SOAResultType.SUCCESS
                    val rewardType = RewardType.all().first()
                    rewardType.audience shouldBe Audience.FULL
                    rewardType.type shouldBe RewardTypeName.EVEN
                    val reward = Reward.all().first()
                    reward.metadatas.count() shouldBe 1
                    reward.metadatas.first().key shouldBe "title"
                    reward.metadatas.first().value shouldBe "reward everyone"
                    reward.type.id shouldBe rewardType.id
                    val pool = RewardPool.all().first()
                    reward.pool.id shouldBe pool.id
                    val completionCriteria = CompletionCriteria.all().first()
                    completionCriteria.reward shouldBe reward
                    completionCriteria.address shouldBe address
                }
            }
        }
    }
}