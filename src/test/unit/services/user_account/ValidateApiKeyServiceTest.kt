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
    private var service = ValidateApiKeyService()
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
                    user = GenerateUserAccountService().execute(null, params).data!!
                    apiCred = user.apiCreds
                }

                // TODO change this to use a decrypted secret
                var apiKeyParams = mutableMapOf(
                    Pair("apiKey", apiCred.apiKey),
                    Pair("secretKey", apiCred.encryptedSecretKey)
                )
                var result = service.execute(user.idValue, Any(), apiKeyParams)
                result.result shouldBe SOAResultType.SUCCESS
            }
            "should return invalid for an invalid secret" {
                transaction {
                    user = GenerateUserAccountService().execute(null, params).data!!
                    apiCred = user.apiCreds
                }

                var apiKeyParams = mutableMapOf(
                        Pair("apiKey", apiCred.apiKey),
                        Pair("secretKey", "FAKESECRET")
                )
                var result = service.execute(user.idValue, Any(), apiKeyParams)
                result.result shouldBe SOAResultType.FAILURE
                result.message shouldBe "Invalid api credentials"
            }
        }
    }
}