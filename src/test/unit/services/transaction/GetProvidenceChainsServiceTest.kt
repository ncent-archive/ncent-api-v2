package test.unit.services.transaction

import io.kotlintest.*
import io.kotlintest.matchers.collections.shouldContainExactly
import io.kotlintest.specs.WordSpec
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.models.Handler
import main.daos.Transaction
import main.services.transaction.GetProvidenceChainsService
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import test.TestHelper

@ExtendWith(MockKExtension::class)
class GetProvidenceChainsServiceTest : WordSpec() {
    private lateinit var middleTransaction: Transaction
    private lateinit var sideTransaction: Transaction

    override fun beforeTest(description: Description): Unit {
        Handler.connectAndBuildTables()
        val transactions = TestHelper.buildGenericProvidenceChain()
        sideTransaction = transactions[2]
        middleTransaction = transactions[1]
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        "calling execute with a valid transaction id" should {
            "return the list of all possible chains if there are many children nodes involved" {
                transaction {
                    val result = GetProvidenceChainsService.execute(middleTransaction)

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
            }
            "return a single chain if there are no children node involved" {
                transaction {
                    val result = GetProvidenceChainsService.execute(sideTransaction)

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
}