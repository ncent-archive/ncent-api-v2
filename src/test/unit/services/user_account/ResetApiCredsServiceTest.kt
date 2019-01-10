package test.unit.services.user_account

import framework.models.idValue
import io.kotlintest.*
import io.kotlintest.specs.WordSpec
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import main.daos.*
import kotlinserverless.framework.models.Handler
import kotlinserverless.framework.services.SOAResultType
import main.services.user_account.ResetApiCredsService
import org.jetbrains.exposed.sql.transactions.transaction
import test.TestHelper

@ExtendWith(MockKExtension::class)
class ResetApiCredsServiceTest : WordSpec() {
    private lateinit var userAccount: UserAccount

    override fun beforeTest(description: Description) {
        Handler.connectAndBuildTables()
        transaction {
            userAccount = TestHelper.generateUserAccounts(1)[0]
        }
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        "calling execute with a valid user id" should {
            "return a success result and an apiNameSpace and generate a transaction" {
                transaction {
                    val apiCredsNamespaceResult = ResetApiCredsService.execute(userAccount.idValue)
                    apiCredsNamespaceResult.result shouldBe SOAResultType.SUCCESS
                    val action = Action.all().toList()[1]
                    action.data shouldBe userAccount.idValue
                    action.type shouldBe ActionType.UPDATE
                    action.dataType shouldBe "UserAccount"
                    Transaction.all().toList()[1].action.id shouldBe action.id
                }
            }

            "reset that user account's api credentials" {
                transaction {
                    val originalApiKey = userAccount.apiCreds.apiKey
                    val apiCredsNamespaceResult = ResetApiCredsService.execute(userAccount.idValue)
                    val updatedApiKey = apiCredsNamespaceResult.data!!.apiKey
                    apiCredsNamespaceResult.result shouldBe SOAResultType.SUCCESS
                    updatedApiKey shouldNotBe originalApiKey
                    UserAccount.findById(userAccount.idValue)!!.apiCreds.apiKey shouldBe updatedApiKey
                }
            }
        }
    }
}