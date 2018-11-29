package test.unit.services.reward

import io.kotlintest.Description
import io.kotlintest.TestResult
import io.kotlintest.matchers.collections.shouldContainExactly
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import io.mockk.junit5.MockKExtension
import kotlinserverless.framework.models.Handler
import kotlinserverless.framework.services.SOAResultType
import main.daos.Audience
import main.daos.RewardTypeName
import main.services.reward.DistributeRewardService
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.extension.ExtendWith
import test.TestHelper

@ExtendWith(MockKExtension::class)
class DistributeRewardServiceTest : WordSpec() {
    private lateinit var rewardId: EntityID<Int>
    private lateinit var providenceChainIds: List<EntityID<Int>>

    override fun beforeTest(description: Description): Unit {
        Handler.connectAndBuildTables()
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        "calling execute with a valid transfer" should {
            "generate a list of transactions transferring to the providence chain evenly" {
                rewardId = TestHelper.buildGenericReward(Audience.PROVIDENCE, RewardTypeName.EVEN)
                providenceChainIds = TestHelper.buildGenericProvidenceChain()

                val result = DistributeRewardService.execute(
                    null,
                    mapOf(
                        Pair("reward_id", rewardId.value.toString()),
                        Pair("transaction_id", providenceChainIds.last().value.toString())
                    )
                )

                result.result shouldBe SOAResultType.SUCCESS

                transaction {
                    val payoutTransactions = result.data!!.transactions
                    payoutTransactions.count() shouldBe 4

                    val resultTransactions = result.data!!.transactions
                    resultTransactions.map { tx -> tx.to }
                        .shouldContainExactly(mutableListOf(
                            "MIKE6", "MIKE4", "MIKE2", "MIKE"
                        ))

                    resultTransactions.map { tx ->
                        tx.metadatas.find { md ->
                            md.key == "amount"
                        }!!.value.toDouble()
                    }.forEach { amount ->
                        amount shouldBe (10.0 / 4.0)
                    }
                }
            }
            "generate a single transaction to one individual when reward type is single" {
                rewardId = TestHelper.buildGenericReward(Audience.PROVIDENCE, RewardTypeName.SINGLE)
                providenceChainIds = TestHelper.buildGenericProvidenceChain()

                val result = DistributeRewardService.execute(
                    null,
                    mapOf(
                        Pair("reward_id", rewardId.value.toString()),
                        Pair("transaction_id", providenceChainIds.last().value.toString())
                    )
                )

                result.result shouldBe SOAResultType.SUCCESS

                transaction {
                    val payoutTransactions = result.data!!.transactions
                    payoutTransactions.count() shouldBe 1

                    val resultTransaction = result.data!!.transactions.first()
                    resultTransaction.to!! shouldBe "MIKE6"
                    resultTransaction.metadatas.find { it.key == "amount" }!!.value.toDouble() shouldBe 10.0
                }
            }
            "generate a list of transactions transferring to the providence chain in n over 2" {
                rewardId = TestHelper.buildGenericReward(Audience.PROVIDENCE, RewardTypeName.N_OVER_2)
                providenceChainIds = TestHelper.buildGenericProvidenceChain()

                val result = DistributeRewardService.execute(
                    null,
                    mapOf(
                        Pair("reward_id", rewardId.value.toString()),
                        Pair("transaction_id", providenceChainIds.last().value.toString())
                    )
                )

                result.result shouldBe SOAResultType.SUCCESS

                transaction {
                    val payoutTransactions = result.data!!.transactions
                    payoutTransactions.count() shouldBe 4

                    val resultTransactions = result.data!!.transactions
                    resultTransactions.map { tx -> tx.to }
                        .shouldContainExactly(mutableListOf(
                            "MIKE6", "MIKE4", "MIKE2", "MIKE"
                        ))

                    val amount = 10.0

                    resultTransactions.map { tx ->
                        tx.metadatas.find { md ->
                            md.key == "amount"
                        }!!.value.toDouble()
                    }.shouldContainExactly(mutableListOf(
                        amount / 2, amount / 4, amount / 8, amount / 16
                    ))
                }
            }
        }
    }
}
