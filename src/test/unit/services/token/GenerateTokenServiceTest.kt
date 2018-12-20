package test.unit.services.token

import io.kotlintest.*
import io.kotlintest.specs.WordSpec
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import kotlinserverless.framework.models.Handler
import main.services.token.GenerateTokenService
import org.jetbrains.exposed.sql.transactions.transaction

@ExtendWith(MockKExtension::class)
class GenerateTokenServiceTest : WordSpec() {
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
        "calling execute with a valid token" should {
            "generate the tokens and associated tokenType" {
                transaction {
                    var result = GenerateTokenService.execute(null, nCentTokenNamespace, null)
                    result.result shouldBe SOAResultType.SUCCESS
                    val tokenType = TokenType.all().first()
                    tokenType.name shouldBe "nCent"
                    tokenType.parentToken shouldBe null
                    tokenType.parentTokenConversionRate shouldBe null
                    val token = Token.all().first()
                    token.amount shouldBe 100
                    token.tokenType shouldBe tokenType

                    ethTokenNamespace = TokenNamespace(
                        amount = 1000,
                        tokenType = TokenTypeNamespace(
                            id = null,
                            name = "eth",
                            parentToken = result.data!!.tokenType.id.value,
                            parentTokenConversionRate = 10.0
                        )
                    )
                    result = GenerateTokenService.execute(null, ethTokenNamespace, null)
                    result.result shouldBe SOAResultType.SUCCESS
                }
            }
        }
    }
}