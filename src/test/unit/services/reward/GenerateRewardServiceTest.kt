package test.unit.services.reward

import io.kotlintest.*
import io.kotlintest.specs.WordSpec
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import kotlinserverless.framework.models.Handler
import main.services.reward.GenerateRewardService
import org.jetbrains.exposed.sql.transactions.transaction

@ExtendWith(MockKExtension::class)
class GenerateRewardServiceTest : WordSpec() {
    private lateinit var rewardNamespace: RewardNamespace

    override fun beforeTest(description: Description): Unit {
        Handler.connectAndBuildTables()
        rewardNamespace = RewardNamespace(
            type = RewardTypeNamespace(
                audience = Audience.FULL,
                type = RewardTypeName.EVEN
            ),
            metadatas = MetadatasListNamespace(
                listOf(MetadatasNamespace("title", "reward everyone"))
            )
        )
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        "calling execute with a valid reward" should {
            "generate the reward and associated reward type and pool" {
                transaction {
                    var result = GenerateRewardService.execute(null, rewardNamespace, null)
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
                    reward.pool?.id shouldBe pool.id
                }
            }
        }
    }
}