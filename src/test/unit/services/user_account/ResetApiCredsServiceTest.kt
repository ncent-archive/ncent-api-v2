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
            "should reset that user account's api credentials" {
                transaction {
                    val originalApiCreds = userAccount.apiCreds
                    var apiCredsResult = ResetApiCredsService.execute(userAccount.idValue)
                    var updatedApiCreds = apiCredsResult.data
                    apiCredsResult.result shouldBe SOAResultType.SUCCESS
                    updatedApiCreds shouldNotBe originalApiCreds
                    UserAccount.findById(userAccount.idValue)!!.apiCreds shouldBe updatedApiCreds
                }
            }
        }
    }
}