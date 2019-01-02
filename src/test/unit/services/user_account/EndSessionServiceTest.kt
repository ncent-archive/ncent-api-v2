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
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.joda.time.DateTimeZone

@ExtendWith(MockKExtension::class)
class EndSessionServiceTest : WordSpec() {
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
                transaction {
                    // Create a user and session
                    val session = GenerateUserAccountService.execute(null, params).data!!.value.session
                    // Validate that the created session ends in the future

                    Session.findById(session.id)!!.expiration.millis
                            .shouldBeGreaterThan(DateTime.now(DateTimeZone.UTC).millis)

                    // End the session via the service
                    var result = EndSessionService.execute(null, session.sessionKey)
                    result.result shouldBe SOAResultType.SUCCESS
                    // Refresh session and validate that it is now expired

                    session.refresh(true)
                    session.expiration.millis
                            .shouldBeLessThanOrEqual(DateTime.now(DateTimeZone.UTC).millis)
                }
            }
        }
    }
}