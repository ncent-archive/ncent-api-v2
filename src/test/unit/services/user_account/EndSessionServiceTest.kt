package test.unit.services.user_account

import io.kotlintest.*
import io.kotlintest.matchers.numerics.shouldBeGreaterThan
import io.kotlintest.matchers.numerics.shouldBeLessThanOrEqual
import io.kotlintest.specs.WordSpec
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import kotlinserverless.framework.models.Handler
import main.services.user_account.EndSessionService
import main.services.user_account.GenerateUserAccountService
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

@ExtendWith(MockKExtension::class)
class EndSessionServiceTest : WordSpec() {
    private var service = EndSessionService()
    private var params = mutableMapOf(
            Pair("email", "dev@ncnt.io"),
            Pair("firstname", "dev"),
            Pair("lastname", "ncnt")
    )

    override fun beforeTest(description: Description): Unit {
        Handler.connectAndBuildTables()
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        "calling execute with a valid session key" should {
            "should expire the session if it is not expired yet" {
                val session = transaction {
                    return@transaction Session.findById(
                        GenerateUserAccountService().execute(null, params).data!!.session
                    )!!
                }
                transaction {
                    Session.findById(session.id)!!.expiration.millis
                            .shouldBeGreaterThan(DateTime.now().millis)
                }
                var result = service.execute(null, session.sessionKey)
                result.result shouldBe SOAResultType.SUCCESS
                transaction {
                    session.refresh(true)
                    session.expiration.millis
                            .shouldBeLessThanOrEqual(DateTime.now().millis)
                }
            }
        }
    }
}