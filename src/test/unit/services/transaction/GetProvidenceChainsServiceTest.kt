package test.unit.services.transaction

import io.kotlintest.*
import io.kotlintest.matchers.collections.shouldContainExactly
import io.kotlintest.specs.WordSpec
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.models.Handler
import main.services.transaction.GetProvidenceChainsService
import org.jetbrains.exposed.dao.EntityID
import test.TestHelper

@ExtendWith(MockKExtension::class)
class GetProvidenceChainsServiceTest : WordSpec() {
    private var service = GetProvidenceChainsService()
    private lateinit var middleTransactionId: EntityID<Int>
    private lateinit var sideTransactionId: EntityID<Int>

    override fun beforeTest(description: Description): Unit {
        Handler.connectAndBuildTables()
        val transactions = TestHelper().buildGenericProvidenceChain()
        sideTransactionId = transactions[2]
        middleTransactionId = transactions[1]
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