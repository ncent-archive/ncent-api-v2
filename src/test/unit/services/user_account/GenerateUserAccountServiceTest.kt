package test.unit.services.user_account

import framework.models.idValue
import io.kotlintest.*
import io.kotlintest.matchers.string.shouldContain
import io.kotlintest.specs.WordSpec
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import main.services.user_account.GenerateUserAccountService
import kotlinserverless.framework.models.Handler
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

@ExtendWith(MockKExtension::class)
class GenerateUserAccountServiceTest : WordSpec() {

    override fun beforeTest(description: Description): Unit {
        Handler.connectAndBuildTables()
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        "calling execute with a valid user account" should {
            "return a success result and new user account and generate a transaction" {
                transaction {
                    var result = GenerateUserAccountService.execute("dev@ncnt.io", "dev", "ncnt")
                    result.result shouldBe SOAResultType.SUCCESS
                    val action = Action.all().first()
                    action.data shouldBe result.data!!.value.idValue
                    action.type shouldBe ActionType.CREATE
                    action.dataType shouldBe "UserAccount"
                    Transaction.all().first().action.id shouldBe action.id
                }
            }
        }

        "calling execute with an invalid user account" should {
            "return a fail result" {
                transaction {
                    var result = GenerateUserAccountService.execute("BADEMAIL", "dev", "ncnt")
                    result.result shouldBe SOAResultType.FAILURE
                    result.message.shouldContain("Check constraint violation")
                }
            }
        }

        "calling execute with an already existing user account" should {
            "return a fail result" {
                transaction {
                    var result = GenerateUserAccountService.execute("dev@ncnt.io", "dev", "ncnt")
                    result.result shouldBe SOAResultType.SUCCESS
                    var result2 = GenerateUserAccountService.execute("dev@ncnt.io", "dev", "ncnt")
                    result2.result shouldBe SOAResultType.FAILURE
                    result2.message.shouldContain("Unique index or primary key violation")
                }
            }
        }
    }
}