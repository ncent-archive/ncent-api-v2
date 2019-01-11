package test.unit.services.token

import io.kotlintest.Description
import io.kotlintest.TestResult
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import io.mockk.junit5.MockKExtension
import kotlinserverless.framework.models.Handler
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import main.services.token.GenerateTokenService
import main.services.token.GetTokenService
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.extension.ExtendWith
import test.TestHelper

@ExtendWith(MockKExtension::class)
class GetTokenServiceTest : WordSpec() {
    private lateinit var nCentTokenNamespace: TokenNamespace
    private lateinit var ethTokenNamespace: TokenNamespace

    override fun beforeTest(description: Description): Unit {
        Handler.connectAndBuildTables()
        nCentTokenNamespace = TokenNamespace(
            amount = 100,
            tokenType = TokenTypeNamespace(
                id = null,
                name = "nCent",
                parentToken = null,
                parentTokenConversionRate = null
            )
        )
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        "calling execute with a valid token name" should {
            "return the token and its parent" {
                transaction {
                    val caller = TestHelper.generateUserAccounts().first()
                    var newTokenResult = GenerateTokenService.execute(caller, nCentTokenNamespace)
                    ethTokenNamespace = TokenNamespace(
                        amount = 1000,
                        tokenType = TokenTypeNamespace(
                            id = null,
                            name = "eth",
                            parentToken = newTokenResult.data!!.tokenType.id.value,
                            parentTokenConversionRate = 10.0
                        )
                    )
                    GenerateTokenService.execute(caller, ethTokenNamespace)

                    var result = GetTokenService.execute("eth")
                    result.result shouldBe SOAResultType.SUCCESS
                    var ethtoken = result.data as Token
                    ethtoken.amount shouldBe 1000
                    ethtoken.tokenType.name shouldBe "eth"
                    ethtoken.tokenType.parentTokenConversionRate shouldBe 10.0
                    ethtoken.tokenType.parentToken!!.name shouldBe "nCent"
                }
            }
        }
    }
}