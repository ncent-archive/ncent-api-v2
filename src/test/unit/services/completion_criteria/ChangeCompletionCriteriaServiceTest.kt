package test.unit.services.completion_criteria

import framework.models.idValue
import io.kotlintest.*
import io.kotlintest.specs.WordSpec
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import kotlinserverless.framework.models.Handler
import main.services.completion_criteria.ChangeCompletionCriteriaService
import main.services.completion_criteria.GenerateCompletionCriteriaService
import main.services.user_account.GenerateUserAccountService
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

@ExtendWith(MockKExtension::class)
class ChangeCompletionCriteriaServiceTest : WordSpec() {
    private lateinit var userAccount: UserAccount
    private lateinit var userAccount2: UserAccount
    private lateinit var completionCriteria: CompletionCriteria

    override fun beforeTest(description: Description): Unit {
        Handler.connectAndBuildTables()
        transaction {
            userAccount = GenerateUserAccountService.execute(null, mutableMapOf(
                Pair("email", "dev@ncnt.io"),
                Pair("firstname", "dev"),
                Pair("lastname", "ncnt")
            )).data!!
            userAccount2 = GenerateUserAccountService.execute(null, mutableMapOf(
                Pair("email", "dev2@ncnt.io"),
                Pair("firstname", "dev2"),
                Pair("lastname", "ncnt2")
            )).data!!
            val rewardNamespace = RewardNamespace(
                type = RewardTypeNamespace(
                    audience = Audience.FULL,
                    type = RewardTypeName.EVEN
                ),
                metadatas = MetadatasListNamespace(
                    listOf(MetadatasNamespace("title", "reward everyone"))
                )
            )
            val completionCriteriaNamespace = CompletionCriteriaNamespace(
                address = userAccount.cryptoKeyPair.publicKey,
                rewardNamespace =  rewardNamespace,
                expiration = DateTime.now().plusMonths(5),
                preReqCompletionCriteriaIds = listOf()
            )
            var result = GenerateCompletionCriteriaService.execute(null, completionCriteriaNamespace, null)
            completionCriteria = result.data!!
        }
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        "calling execute" should {
            "succeed with the correct address passed" {
                transaction {
                    val result = ChangeCompletionCriteriaService.execute(userAccount.idValue, mapOf(
                        Pair("completion_criteria_id", completionCriteria.idValue.toString()),
                        Pair("new_completion_criteria_address", userAccount2.cryptoKeyPair.publicKey)
                    ))
                    result.result shouldBe SOAResultType.SUCCESS
                    result.data!!.address shouldBe userAccount2.cryptoKeyPair.publicKey
                }
            }
            "fail with the incorrect address passed" {
                transaction {
                    val result = ChangeCompletionCriteriaService.execute(userAccount2.idValue, mapOf(
                        Pair("completion_criteria_id", completionCriteria.idValue.toString()),
                        Pair("new_completion_criteria_address", userAccount.cryptoKeyPair.publicKey)
                    ))
                    result.result shouldBe SOAResultType.FAILURE
                }
            }
        }
    }
}