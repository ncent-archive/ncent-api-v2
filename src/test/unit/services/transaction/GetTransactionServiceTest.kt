package test.unit.services.transaction

import framework.models.idValue
import io.kotlintest.*
import io.kotlintest.matchers.collections.shouldContainExactly
import io.kotlintest.specs.WordSpec
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import kotlinserverless.framework.models.Handler
import main.services.transaction.GenerateTransactionService
import main.services.transaction.GetTransactionService
import org.jetbrains.exposed.sql.transactions.transaction

@ExtendWith(MockKExtension::class)
class GetTransactionServiceTest : WordSpec() {
    private lateinit var transactionNamespace: TransactionNamespace
    private lateinit var transaction2Namespace: TransactionNamespace

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
            metadatas = arrayOf(
                    MetadatasNamespace("city", "san carlos"),
                    MetadatasNamespace("state", "california")
            )
        )
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        "calling execute with a valid transaction id" should {
            "return the transaction and associated objects" {
                transaction {
                    var parentTxGenerateResult = GenerateTransactionService.execute(transactionNamespace)
                    transaction2Namespace = TransactionNamespace(
                        from = "ARYA2",
                        to = "MIKE",
                        action = ActionNamespace(
                            type = ActionType.CREATE,
                            data = 2,
                            dataType = "UserAccount"
                        ),
                        previousTransaction = parentTxGenerateResult.data!!.idValue,
                        metadatas = arrayOf(
                                MetadatasNamespace("city", "san carlos"),
                                MetadatasNamespace("state", "california")
                        )
                    )
                    val txGenerateResult = GenerateTransactionService.execute(transaction2Namespace)
                    val result = GetTransactionService.execute(txGenerateResult.data!!.idValue)

                    result.result shouldBe SOAResultType.SUCCESS
                    val tx = result.data!!
                    tx.from shouldBe "ARYA2"
                    tx.previousTransaction!!.from shouldBe "ARYA"
                    tx.previousTransaction!!.action.data shouldBe 1
                    tx.to shouldBe "MIKE"
                    tx.action.data shouldBe 2
                    tx.action.dataType shouldBe "UserAccount"
                    tx.action.type shouldBe ActionType.CREATE
                    tx.metadatas.count() shouldBe 2
                    tx.metadatas.map { md -> Pair(md.key, md.value) }
                        .shouldContainExactly(
                            listOf(Pair("city", "san carlos"), Pair("state", "california"))
                        )
                }
            }
        }
    }
}