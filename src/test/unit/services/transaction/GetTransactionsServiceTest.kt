package test.unit.services.transaction

import io.kotlintest.*
import io.kotlintest.matchers.collections.shouldContainExactly
import io.kotlintest.specs.WordSpec
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import kotlinserverless.framework.models.Handler
import main.services.transaction.GenerateTransactionService
import main.services.transaction.GetTransactionsService
import org.jetbrains.exposed.sql.transactions.transaction

@ExtendWith(MockKExtension::class)
class GetTransactionsServiceTest : WordSpec() {
    private lateinit var transactionNamespace: TransactionNamespace
    private lateinit var transaction2Namespace: TransactionNamespace

    override fun beforeTest(description: Description): Unit {
        Handler.connectAndBuildTables()
        transactionNamespace = TransactionNamespace(
            from = "ARYA",
            to = "MIKE",
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

        transaction2Namespace = TransactionNamespace(
            from = "ARYA2",
            to = "MIKE",
            action = ActionNamespace(
                type = ActionType.CREATE,
                data = 2,
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
                    GenerateTransactionService.execute(transactionNamespace)
                    GenerateTransactionService.execute(transaction2Namespace)
                    val result = GetTransactionsService.execute(from = null, to = "MIKE", previousTxId = null, actionNamespace = null)

                    result.result shouldBe SOAResultType.SUCCESS
                    val txs = result.data!!
                    txs.transactions.count() shouldBe 2
                    txs.transactions.forEach {
                        it.to shouldBe "MIKE"
                        it.previousTransaction shouldBe null
                        it.action.dataType shouldBe "UserAccount"
                        it.action.type shouldBe ActionType.CREATE
                        it.metadatas.count() shouldBe 2
                        it.metadatas.map { md -> Pair(md.key, md.value) }
                            .shouldContainExactly(
                                listOf(Pair("city", "san carlos"), Pair("state", "california"))
                            )
                    }
                }
            }
        }
    }
}