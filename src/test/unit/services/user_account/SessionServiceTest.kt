package test.unit.services.user_account

import framework.models.idValue
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import io.kotlintest.Description
import io.kotlintest.days
import io.kotlintest.eventually
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.junit.jupiter.api.extension.ExtendWith
import kotlinserverless.framework.services.SOAResultType
import main.daos.ApiCred
import main.daos.UserAccount
import main.services.user_account.EndSessionService
import main.services.user_account.StartSessionService
import main.services.user_account.ValidateSessionService

@ExtendWith(MockKExtension::class)
class SessionServiceTest : WordSpec() {
    private val secretKey = "SomeSecretKey"
    private lateinit var userAccount: UserAccount
    private lateinit var apiCred: ApiCred

    override fun beforeTest(description: Description): Unit {
//        apiCred = ApiCred.new {
//            apiKey = "ASDF"
//            encryptedSecretKey = secretKey
//        }
//        // TODO fill this out with all of the info needed
//        userAccount = UserAccount.new {
//            apiCreds = apiCred.id
//        }
    }

    init {
        "!Start session service" should {
            "return success session when using a valid api key pair" {
                val result = StartSessionService.execute()
                result.result shouldBe SOAResultType.SUCCESS
            }

            "return success and the same session if a session is still open" {
                var result = StartSessionService.execute()
                result.result shouldBe SOAResultType.SUCCESS
                val session = result.data
                result = StartSessionService.execute()
                result.result shouldBe SOAResultType.SUCCESS
                result.data shouldBe session
            }

            "return failure when using an invalid api key pair" {
                val result = StartSessionService.execute()
                result.result shouldBe SOAResultType.FAILURE
            }
        }

        "!End session service" should {
            "return success when passing a valid session" {
                val session = StartSessionService.execute().data!!
                val result = EndSessionService.execute(session.sessionKey)
                result.result shouldBe SOAResultType.SUCCESS
            }

            "return failure when passing an invalid session" {
                val session = StartSessionService.execute().data!!
                val result = EndSessionService.execute("BADSESSIONKEY")
                result.result shouldBe SOAResultType.FAILURE
            }

            "return failure when passing an expired session" {
                val session = StartSessionService.execute().data!!
                eventually(31.days) {
                    var result = EndSessionService.execute(session.sessionKey)
                    result.result shouldBe SOAResultType.FAILURE
                    result.message shouldBe "Session has expired."
                }
            }
        }

        "!Validate session service" should {
            "return success when passing a valid session" {
                val session = StartSessionService.execute().data!!
                val result = ValidateSessionService.execute(userAccount, session.sessionKey)
                result.result shouldBe SOAResultType.SUCCESS
            }

            "return failure when passing an invalid session" {
                val session = StartSessionService.execute().data!!
                val result = ValidateSessionService.execute(userAccount, "BADSESSIONKEY")
                result.result shouldBe SOAResultType.FAILURE
            }

            "return failure when passing an expired session" {
                val session = StartSessionService.execute().data!!
                eventually(31.days) {
                    var result = ValidateSessionService.execute(userAccount, session.sessionKey)
                    result.result shouldBe SOAResultType.FAILURE
                    result.message shouldBe "Session has expired."
                }
            }
        }
    }
}