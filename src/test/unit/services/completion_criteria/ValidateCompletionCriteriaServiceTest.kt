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
import main.services.completion_criteria.ValidateCompletionCriteriaService
import main.services.user_account.GenerateUserAccountService
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import test.TestHelper

@ExtendWith(MockKExtension::class)
class ValidateCompletionCriteriaServiceTest : WordSpec() {
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
            TestHelper.buildGenericReward()
            completionCriteria = CompletionCriteria.all().first()
            completionCriteria.address = userAccount.cryptoKeyPair.publicKey
        }
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        "calling execute" should {
            "succeed with the correct address passed" {
                transaction {
                    val result = ValidateCompletionCriteriaService.execute(userAccount.idValue, mapOf(
                        Pair("completion_criteria_id", completionCriteria.idValue.toString())
                    ))
                    result.result shouldBe SOAResultType.SUCCESS
                    result.data!! shouldBe true
                }
            }
            "fail with the incorrect address passed" {
                transaction {
                    val result = ValidateCompletionCriteriaService.execute(userAccount2.idValue, mapOf(
                        Pair("completion_criteria_id", completionCriteria.idValue.toString())
                    ))
                    result.result shouldBe SOAResultType.SUCCESS
                    result.data!! shouldBe false
                }
            }
        }
    }
}