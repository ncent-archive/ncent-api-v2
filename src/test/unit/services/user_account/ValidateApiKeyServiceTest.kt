package test.unit.services.user_account

import io.kotlintest.*
import io.kotlintest.specs.WordSpec
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import main.daos.*
import kotlinserverless.framework.models.Handler
import kotlinserverless.framework.services.SOAResultType
import main.services.user_account.GenerateUserAccountService
import main.services.user_account.ValidateApiKeyService
import org.jetbrains.exposed.sql.transactions.transaction

@ExtendWith(MockKExtension::class)
class ValidateApiKeyServiceTest : WordSpec() {
    private lateinit var apiCred: ApiCred
    private lateinit var user: UserAccount

    override fun beforeTest(description: Description): Unit {
        Handler.connectAndBuildTables()
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        "executing validate api key service" should {
            "should return valid for a valid api key/session key combo" {
                transaction {
                    var result = GenerateUserAccountService.execute("dev@ncnt.io", "dev", "ncnt").data!!
                    user = result.value
                    apiCred = user.apiCreds

                    var result2 = ValidateApiKeyService.execute(apiCred.apiKey, result.secretKey)
                    result2.result shouldBe SOAResultType.SUCCESS
                }
            }
            "should return invalid for an invalid secret" {
                transaction {
                    var result = GenerateUserAccountService.execute("dev@ncnt.io", "dev", "ncnt").data!!
                    user = result.value
                    apiCred = user.apiCreds

                    var result2 = ValidateApiKeyService.execute(apiCred.apiKey, "FAKESECRET")
                    result2.result shouldBe SOAResultType.FAILURE
                    result2.message shouldBe "Invalid api credentials"
                }
            }
        }
    }
}