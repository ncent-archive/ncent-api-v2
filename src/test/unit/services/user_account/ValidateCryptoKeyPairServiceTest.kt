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
import main.services.user_account.ValidateCryptoKeyPairService
import org.jetbrains.exposed.sql.transactions.transaction

@ExtendWith(MockKExtension::class)
class ValidateCryptoKeyPairServiceTest : WordSpec() {
    private lateinit var cryptoKeyPair: CryptoKeyPair
    private lateinit var user: UserAccount

    override fun beforeTest(description: Description): Unit {
        Handler.connectAndBuildTables()
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        "executing validate crypto key pair service" should {
            "should return valid for a valid pub/priv key combo" {
                transaction {
                    var result = GenerateUserAccountService.execute("dev@ncnt.io", "dev", "ncnt").data!!
                    user = result.value
                    cryptoKeyPair = user.cryptoKeyPair!!

                    var result2 = ValidateCryptoKeyPairService.execute(cryptoKeyPair.publicKey, result.privateKey)
                    result2.result shouldBe SOAResultType.SUCCESS
                }
            }
            "should return invalid for an invalid secret" {
                transaction {
                    var result = GenerateUserAccountService.execute("dev@ncnt.io", "dev", "ncnt").data!!
                    user = result.value
                    cryptoKeyPair = user.cryptoKeyPair!!

                    var result2 = ValidateCryptoKeyPairService.execute(cryptoKeyPair.publicKey, "FAKEPRIVATEKEY")
                    result2.result shouldBe SOAResultType.FAILURE
                    result2.message shouldBe "Invalid key pair"
                }
            }
        }
    }
}