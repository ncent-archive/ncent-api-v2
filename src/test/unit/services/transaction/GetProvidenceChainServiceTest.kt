package test.unit.services.transaction

import framework.models.idValue
import io.kotlintest.*
import io.kotlintest.matchers.collections.shouldContainExactly
import io.kotlintest.specs.WordSpec
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.models.Handler
import main.services.transaction.GetProvidenceChainService
import org.jetbrains.exposed.dao.EntityID
import test.TestHelper

@ExtendWith(MockKExtension::class)
class GetProvidenceChainServiceTest : WordSpec() {
    private lateinit var endTransactionId: EntityID<Int>
    private lateinit var sideTransactionId: EntityID<Int>

    override fun beforeTest(description: Description): Unit {
        Handler.connectAndBuildTables()
        val transactions = TestHelper.buildGenericProvidenceChain()
        endTransactionId = transactions[5]
        sideTransactionId = transactions[3]
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        "calling execute with a valid transaction id" should {
            "return the providence chain" {
                val result = GetProvidenceChainService.execute(null, endTransactionId.value)

                result.result shouldBe SOAResultType.SUCCESS

                val expectedResult = mutableListOf(
                    "ARYA", "ARYA2", "ARYA4", "ARYA6"
                )

                result.data!!.transactions.map { t -> t.from }
                    .shouldContainExactly(expectedResult)
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