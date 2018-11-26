package test.unit.services.reward

import framework.models.idValue
import io.kotlintest.Description
import io.kotlintest.TestResult
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import io.mockk.junit5.MockKExtension
import kotlinserverless.framework.models.Handler
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import main.services.reward.AddToRewardPoolService
import main.services.reward.GenerateRewardService
import main.services.token.GenerateTokenService
import main.services.user_account.GenerateUserAccountService
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class AddToRewardPoolServiceTest : WordSpec() {
    private var service = AddToRewardPoolService()
    private lateinit var rewardNamespace: RewardNamespace
    private lateinit var nCentTokenNamespace: TokenNamespace

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
        nCentTokenNamespace = TokenNamespace(
                amount = 100,
                tokenType = TokenTypeNamespace(
                        id = null,
                        name = "nCent",
                        parentToken = null,
                        parentTokenConversionRate = null
                )
        )
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        "calling execute with a valid transfer" should {
            "generate a transaction transfering to the pool" {
                transaction {
                    var newUserAccount = GenerateUserAccountService().execute(
                        null,
                        mapOf(
                            Pair("firstname", "Arya"),
                            Pair("lastname", "Soltanieh"),
                            Pair("email", "as@ncent.io")
                        )
                    ).data!!
                    val token = GenerateTokenService().execute(newUserAccount.idValue, nCentTokenNamespace, null).data!!
                    var reward = GenerateRewardService().execute(newUserAccount.idValue, rewardNamespace, null).data!!
                    val result = service.execute(
                        newUserAccount.idValue,
                        mapOf(
                            Pair("reward_id", reward.idValue.toString()),
                            Pair("name", "nCent"),
                            Pair("amount", "10")
                        )
                    )
                    result.result shouldBe SOAResultType.SUCCESS

                    val transaction = result.data!!
                    transaction.action.type shouldBe ActionType.TRANSFER
                    transaction.from shouldBe newUserAccount.cryptoKeyPair.publicKey
                    transaction.to shouldBe reward.pool.cryptoKeyPair.publicKey
                    transaction.metadatas.first().value shouldBe "10.0"
                    transaction.action.data shouldBe token.idValue
                }
            }
        }
    }
}
