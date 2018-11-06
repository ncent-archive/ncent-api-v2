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
import main.services.transaction.GetProvidenceChainsService
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

@ExtendWith(MockKExtension::class)
class GetProvidenceChainsServiceTest : WordSpec() {
    private var service = GetProvidenceChainsService()
    private lateinit var transactionNamespace: TransactionNamespace
    private lateinit var transaction2Namespace: TransactionNamespace
    private lateinit var transaction3Namespace: TransactionNamespace
    private lateinit var transaction4Namespace: TransactionNamespace
    private lateinit var transaction5Namespace: TransactionNamespace
    private lateinit var transaction6Namespace: TransactionNamespace
    private lateinit var action: ActionNamespace
    private lateinit var metadatas: MetadatasListNamespace
    private lateinit var middleTransactionId: EntityID<Int>
    private lateinit var sideTransactionId: EntityID<Int>

    override fun beforeTest(description: Description): Unit {
        Handler.connectAndBuildTables()
        action = ActionNamespace(
            type = ActionType.CREATE,
            data = 1,
            dataType = "UserAccount"
        )
        metadatas = MetadatasListNamespace(
            listOf(
                MetadatasNamespace("city", "san carlos"),
                MetadatasNamespace("state", "california")
            )
        )

        /**
         * Building a tree:
         *         ARYA
         *           |
         *         ARYA2
         *         /    \
         *      ARYA3   ARYA4
         *             /    \
         *          ARYA5   ARYA6
         */
        transaction {
            transactionNamespace = TransactionNamespace(from = "ARYA", to = "MIKE", action = action, previousTransaction = null, metadatas = metadatas)
            var tx = GenerateTransactionService().execute(null, transactionNamespace, null).data!!
            transaction2Namespace = TransactionNamespace(from = "ARYA2", to = "MIKE", action = action, previousTransaction = tx.idValue, metadatas = metadatas)
            tx = GenerateTransactionService().execute(null, transaction2Namespace, null).data!!
            middleTransactionId = tx.id
            transaction3Namespace = TransactionNamespace(from = "ARYA3", to = "MIKE", action = action, previousTransaction = tx.idValue, metadatas = metadatas)
            transaction4Namespace = TransactionNamespace(from = "ARYA4", to = "MIKE", action = action, previousTransaction = tx.idValue, metadatas = metadatas)
            tx = GenerateTransactionService().execute(null, transaction3Namespace, null).data!!
            sideTransactionId = tx.id
            tx = GenerateTransactionService().execute(null, transaction4Namespace, null).data!!
            transaction5Namespace = TransactionNamespace(from = "ARYA5", to = "MIKE", action = action, previousTransaction = tx.idValue, metadatas = metadatas)
            transaction6Namespace = TransactionNamespace(from = "ARYA6", to = "MIKE", action = action, previousTransaction = tx.idValue, metadatas = metadatas)
            GenerateTransactionService().execute(null, transaction5Namespace, null).data!!
            GenerateTransactionService().execute(null, transaction6Namespace, null).data!!
        }
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        "calling execute with a valid transaction id" should {
            "return the list of all possible chains if there are many children nodes involved" {
                val result = service.execute(null, middleTransactionId.value)

                result.result shouldBe SOAResultType.SUCCESS
                result.data!!.count() shouldBe 3

                val expectedResult = mutableListOf<List<String>>()
                expectedResult.add(mutableListOf(
                    "ARYA", "ARYA2", "ARYA3"
                ))
                expectedResult.add(mutableListOf(
                    "ARYA", "ARYA2", "ARYA4", "ARYA5"
                ))
                expectedResult.add(mutableListOf(
                    "ARYA", "ARYA2", "ARYA4", "ARYA6"
                ))

                result.data!!.map { tl ->
                    tl.transactions.map { t ->
                        t.from
                    }
                }.shouldContainExactly(expectedResult)
            }
            "return a single chain if there are no children node involved" {
                val result = service.execute(null, sideTransactionId.value)

                result.result shouldBe SOAResultType.SUCCESS
                result.data!!.count() shouldBe 1

                val expectedResult = mutableListOf<List<String>>()
                expectedResult.add(mutableListOf(
                        "ARYA", "ARYA2", "ARYA3"
                ))

                result.data!!.map { tl ->
                    tl.transactions.map { t ->
                        t.from
                    }
                }.shouldContainExactly(expectedResult)
            }
        }
    }
}