package test.unit.services.transaction

import io.kotlintest.*
import io.kotlintest.matchers.collections.shouldContainExactly
import io.kotlintest.specs.WordSpec
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.models.Handler
import main.daos.Transaction
import main.services.transaction.GetProvidenceChainService
import org.jetbrains.exposed.sql.transactions.transaction
import test.TestHelper

@ExtendWith(MockKExtension::class)
class GetProvidenceChainServiceTest : WordSpec() {
    private lateinit var endTransaction: Transaction
    private lateinit var sideTransaction: Transaction

    override fun beforeTest(description: Description): Unit {
        Handler.connectAndBuildTables()
        val transactions = TestHelper.buildGenericProvidenceChain()
        endTransaction = transactions[5]
        sideTransaction = transactions[3]
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        "calling execute with a valid transaction id" should {
            "return the providence chain" {
                transaction {
                    val result = GetProvidenceChainService.execute(endTransaction)

                    result.result shouldBe SOAResultType.SUCCESS

                    val expectedResult = mutableListOf(
                        "ARYA", "ARYA2", "ARYA4", "ARYA6"
                    )

                    result.data!!.transactions.map { t -> t.from }
                        .shouldContainExactly(expectedResult)
                }
            }
        }
        // TODO decide if we need this later
//        "calling execute with an invalid transaction id" should {
//            "return an error" {
//                val result = GetProvidenceChainService.execute(null, sideTransactionId.value)
//
//                result.result shouldBe SOAResultType.FAILURE
//                result.message shouldBe "Must send a leaf node, must not have children"
//            }
//        }
    }
}