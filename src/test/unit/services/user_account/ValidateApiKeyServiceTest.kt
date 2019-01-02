package test.unit.services.user_account

import framework.models.idValue
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
    private var params = mutableMapOf(
        Pair("email", "dev@ncnt.io"),
        Pair("firstname", "dev"),
        Pair("lastname", "ncnt")
    )
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
                    var result = GenerateUserAccountService.execute(null, params).data!!
                    user = result.value
                    apiCred = user.apiCreds

                    // TODO change this to use a decrypted secret
                    var apiKeyParams = mutableMapOf(
                        Pair("apiKey", apiCred.apiKey),
                        Pair("secretKey", result.secretKey)
                    )
                    var result2 = ValidateApiKeyService.execute(user.idValue, Any(), apiKeyParams)
                    result2.result shouldBe SOAResultType.SUCCESS
                }
            }
            "should return invalid for an invalid secret" {
                transaction {
                    var result = GenerateUserAccountService.execute(null, params).data!!
                    user = result.value
                    apiCred = user.apiCreds

                    var apiKeyParams = mutableMapOf(
                        Pair("apiKey", apiCred.apiKey),
                        Pair("secretKey", "FAKESECRET")
                    )
                    var result2 = ValidateApiKeyService.execute(user.idValue, Any(), apiKeyParams)
                    result2.result shouldBe SOAResultType.FAILURE
                    result2.message shouldBe "Invalid api credentials"
                }
            }
        }
    }
}