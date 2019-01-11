package test.unit.services.user_account

import framework.models.idValue
import io.kotlintest.*
import io.kotlintest.specs.WordSpec
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import main.daos.*
import kotlinserverless.framework.models.Handler
import kotlinserverless.framework.services.SOAResultType
import main.services.user_account.EndSessionService
import main.services.user_account.GenerateUserAccountService
import main.services.user_account.ValidateSessionService
import org.jetbrains.exposed.sql.transactions.transaction

@ExtendWith(MockKExtension::class)
class ValidateSessionServiceTest : WordSpec() {
    private lateinit var session: Session
    private lateinit var user: UserAccount

    override fun beforeTest(description: Description): Unit {
        Handler.connectAndBuildTables()
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        "executing validate session service" should {
            "should return valid for a valid session" {
                transaction {
                    var result = GenerateUserAccountService.execute("dev@ncnt.io", "dev", "ncnt").data!!

                    user = result.value
                    session = user.session!!

                    var result2 = ValidateSessionService.execute(user, session.sessionKey)
                    result2.result shouldBe SOAResultType.SUCCESS
                }
            }
            "should return invalid for a sessionkey that is not associated with the caller" {
                transaction {
                    var result = GenerateUserAccountService.execute("dev@ncnt.io", "dev", "ncnt").data!!
                    user = result.value
                    session = user.session!!
                    var result2 = ValidateSessionService.execute(user, "SOMERANDOMKEY")
                    result2.result shouldBe SOAResultType.FAILURE
                    result2.message shouldBe "Invalid Session"
                }
            }
            "should return invalid for an expired sessionkey" {
                transaction {
                    var result = GenerateUserAccountService.execute("dev@ncnt.io", "dev", "ncnt").data!!
                    user = result.value
                    session = user.session!!
                    EndSessionService.execute(session.sessionKey)
                    var result2 = ValidateSessionService.execute(user, session.sessionKey)
                    result2.result shouldBe SOAResultType.FAILURE
                    result2.message shouldBe "Session Expired"
                }
            }
        }
    }
}