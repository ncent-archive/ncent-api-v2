package test.unit.services.token

import framework.models.idValue
import io.kotlintest.Description
import io.kotlintest.TestResult
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import io.mockk.junit5.MockKExtension
import kotlinserverless.framework.models.Handler
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import main.services.token.GenerateTokenService
import main.services.token.TransferAllTokensService
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.extension.ExtendWith
import test.TestHelper

@ExtendWith(MockKExtension::class)
class TransferAllTokensServiceTest : WordSpec() {
    private lateinit var fooTokenNamespace: TokenNamespace
    private lateinit var barTokenNamespace: TokenNamespace

    override fun beforeTest(description: Description): Unit {
        Handler.connectAndBuildTables()
        fooTokenNamespace = TokenNamespace(
                amount = 100,
                tokenType = TokenTypeNamespace(
                        id = null,
                        name = "foo",
                        parentToken = null,
                        parentTokenConversionRate = null
                )
        )
        barTokenNamespace = TokenNamespace(
                amount = 100,
                tokenType = TokenTypeNamespace(
                        id = null,
                        name = "bar",
                        parentToken = null,
                        parentTokenConversionRate = null
                )
        )
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        "calling execute with a funded user" should {
            "return the transaction generated" {
                val userAccounts = TestHelper.generateUserAccounts(2)
                val newUserAccount = userAccounts[0]
                val newUserAccount2 = userAccounts[1]

                transaction {
                    val fooToken = GenerateTokenService.execute(newUserAccount, fooTokenNamespace).data!!
                    val barToken = GenerateTokenService.execute(newUserAccount, barTokenNamespace).data!!

                    var result = TransferAllTokensService.execute(
                            newUserAccount,
                            newUserAccount2.cryptoKeyPair.publicKey)
                    result.result shouldBe SOAResultType.SUCCESS
                    val tx = result.data!!.transactions
                    tx.size shouldBe 2
                    for(i in 0..1) {
                        tx[i].from shouldBe newUserAccount.cryptoKeyPair.publicKey
                        tx[i].to shouldBe newUserAccount2.cryptoKeyPair.publicKey
                        tx[i].action.type shouldBe ActionType.TRANSFER
                        tx[i].action.dataType shouldBe Token::class.simpleName!!
                        tx[i].metadatas.first().value shouldBe "100.0"
                    }
                    tx[0].action.data shouldBe fooToken.idValue
                    tx[1].action.data shouldBe barToken.idValue
                }
            }
        }

        "calling execute with an unfunded user" should {
            "return return the transaction generated" {
                val userAccounts = TestHelper.generateUserAccounts(2)
                val newUserAccount = userAccounts[0]
                val newUserAccount2 = userAccounts[1]

                transaction {
                    var result = TransferAllTokensService.execute(
                            newUserAccount,
                            newUserAccount2.cryptoKeyPair.publicKey,
                            null)
                    result.result shouldBe SOAResultType.SUCCESS
                    result.data!!.transactions.size shouldBe 0
                    result.message shouldBe "Address has no associated balances so no tokens were transferred."
                }
            }
        }
    }
}