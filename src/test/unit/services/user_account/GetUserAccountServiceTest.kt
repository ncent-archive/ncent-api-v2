package test.unit.services.user_account

import framework.models.idValue
import io.kotlintest.Description
import io.kotlintest.TestResult
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import io.mockk.junit5.MockKExtension
import kotlinserverless.framework.models.Handler
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import main.services.user_account.GetUserAccountService
import test.TestHelper
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class GetUserAccountServiceTest: WordSpec() {
    override fun beforeTest(description: Description): Unit {
        Handler.connectAndBuildTables()
        TestHelper.generateUserAccounts()
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        "calling execute with a valid user account" should {
            transaction {
                val userAccount = UserAccount.all().first()
                val getUserAccountResult = GetUserAccountService.execute(userAccount.idValue, null, null)
                getUserAccountResult.result shouldBe SOAResultType.SUCCESS
                getUserAccountResult.data?.idValue shouldBe userAccount.idValue
            }
        }
        "calling execute with a valid user account email" should {
            transaction {
                val userAccount = UserAccount.all().first()
                val getUserAccountResult = GetUserAccountService.execute(null, userAccount.userMetadata.email, null)
                getUserAccountResult.result shouldBe SOAResultType.SUCCESS
                getUserAccountResult.data?.idValue shouldBe userAccount.idValue
            }
        }
        "calling execute with a valid user account apiKey" should {
            transaction {
                val userAccount = UserAccount.all().first()
                val getUserAccountResult = GetUserAccountService.execute(null, null, userAccount.apiCreds.apiKey)
                getUserAccountResult.result shouldBe SOAResultType.SUCCESS
                getUserAccountResult.data?.idValue shouldBe userAccount.idValue
            }
        }
    }
}
