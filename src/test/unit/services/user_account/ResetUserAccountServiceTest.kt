package test.unit.services.user_account

import framework.models.idValue
import io.kotlintest.*
import io.kotlintest.specs.WordSpec
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import main.daos.*
import main.helpers.EncryptionHelper
import kotlinserverless.framework.models.Handler
import kotlinserverless.framework.services.SOAResultType
import main.services.user_account.ResetUserAccount
import org.jetbrains.exposed.sql.transactions.transaction
import test.TestHelper

@ExtendWith(MockKExtension::class)
class ResetUserAccountServiceTest : WordSpec() {
    private lateinit var userAccount: UserAccount

    override fun beforeTest(description: Description) {
        Handler.connectAndBuildTables()
        val newUserAccounts = TestHelper.generateUserAccounts(1)
        userAccount = newUserAccounts[0].value
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        "calling execute with a valid user account" should {
            "return a success result with the user's new private and secret keys" {
                transaction {
                    val originalPrivateKey = userAccount.cryptoKeyPair.privateKey
                    val originalSecretKey = userAccount.apiCreds.secretKey

                    // Reset the user's account.
                    val newUserAccount= ResetUserAccount.execute(userAccount)
                    val newPrivateKey = newUserAccount.data!!.privateKey
                    val newPrivateKeySalt = newUserAccount.data!!.value.cryptoKeyPair._privateKeySalt
                    val newSecretKey = newUserAccount.data!!.secretKey
                    val newSecretKeySalt = newUserAccount.data!!.value.apiCreds._secretKeySalt

                    // Pull the user account stored in the database.
                    val updatedUserAccount = UserAccount.findById(userAccount.idValue)!!
                    val encryptedPrivateKey = updatedUserAccount.cryptoKeyPair.privateKey
                    val encryptedSecretKey = updatedUserAccount.apiCreds.secretKey

                    // Verify that the user's account was updated in the database and that its credentials are
                    // different than its original credentials.
                    newUserAccount.result shouldBe SOAResultType.SUCCESS
                    EncryptionHelper
                            .validateEncryption(newPrivateKey, newPrivateKeySalt , encryptedPrivateKey) shouldBe true
                    encryptedPrivateKey shouldNotBe originalPrivateKey
                    EncryptionHelper
                            .validateEncryption(newSecretKey, newSecretKeySalt , encryptedSecretKey) shouldBe true
                    encryptedSecretKey shouldNotBe originalSecretKey
                }
            }
        }
    }
}