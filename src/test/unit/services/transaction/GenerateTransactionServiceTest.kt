package test.unit.services.transaction

import io.kotlintest.*
import io.kotlintest.specs.WordSpec
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import kotlinserverless.framework.models.Handler
import main.services.transaction.GenerateTransactionService
import org.jetbrains.exposed.sql.transactions.transaction

@ExtendWith(MockKExtension::class)
class GenerateTransactionServiceTest : WordSpec() {
    private lateinit var transactionNamespace: TransactionNamespace

    override fun beforeTest(description: Description): Unit {
        Handler.connectAndBuildTables()
        transactionNamespace = TransactionNamespace(
            from = "ARYA",
            to = null,
            action = ActionNamespace(
                type = ActionType.CREATE,
                data = 1,
                dataType = "UserAccount"
            ),
            previousTransaction = null,
            metadatas = MetadatasListNamespace(
                listOf(
                    MetadatasNamespace("city", "san carlos"),
                    MetadatasNamespace("state", "california")
                )
            )
        )
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        "calling execute with a valid transaction" should {
            "generate the transaction and associated action" {
                transaction {
                    var result = GenerateTransactionService.execute(null, transactionNamespace, null)
                    result.result shouldBe SOAResultType.SUCCESS
                    val action = Action.all().first()
                    action.data shouldBe 1
                    action.type shouldBe ActionType.CREATE
                    action.dataType shouldBe "UserAccount"
                    val transaction = Transaction.all().first()
                    transaction.action.id shouldBe action.id
                    transaction.from shouldBe "ARYA"
                    Metadata.all().count() shouldBe 2
                    Metadata.all().distinct().map {
                        md -> Pair(md.key, md.value)
                    } shouldBe listOf(Pair("city", "san carlos"), Pair("state", "california"))
                }
            }
        }
    }
}