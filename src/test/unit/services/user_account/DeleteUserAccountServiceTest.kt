package test.unit.services.user_account

import io.kotlintest.*
import io.kotlintest.specs.WordSpec
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import kotlinserverless.framework.services.SOAResultType
import main.services.user_account.GenerateUserAccountService
import kotlinserverless.framework.models.Handler
import main.services.user_account.DeleteUserAccountService
import main.services.user_account.GetUserAccountService
import org.jetbrains.exposed.sql.transactions.transaction

@ExtendWith(MockKExtension::class)
class DeleteUserAccountServiceTest : WordSpec() {

    override fun beforeTest(description: Description): Unit {
        Handler.connectAndBuildTables()
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        "calling execute with a valid user account" should {
            "return a success result and a deleted user account" {
                transaction {
                    val user = GenerateUserAccountService.execute("dev@ncnt.io", "dev", "ncnt").data!!.value
                    val result = DeleteUserAccountService.execute(user)
                    result.result shouldBe SOAResultType.SUCCESS
                }

                transaction {
                    val getUserResult = GetUserAccountService.execute(email = "dev@ncnt.io")
                    getUserResult.data shouldBe null
                    val userRepeatResult = GenerateUserAccountService.execute("dev@ncnt.io", "dev", "ncnt")
                    userRepeatResult.result shouldBe SOAResultType.SUCCESS
                }
            }
        }
    }
}