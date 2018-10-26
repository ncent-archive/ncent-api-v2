package test.unit.services.user_account

import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import io.kotlintest.Description
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.junit.jupiter.api.extension.ExtendWith
import kotlinserverless.framework.services.SOAResultType
import main.daos.User
import main.daos.UserAccount
import main.services.user_account.GenerateUserAccountService

@ExtendWith(MockKExtension::class)
class GenerateUserAccountServiceTest : WordSpec() {
    private var service = GenerateUserAccountService()
    private lateinit var userAccount: UserAccount

    override fun beforeTest(description: Description): Unit {
        userAccount = mockk()
    }

    init {
        "!calling execute with a valid user account" should {
            "return a success result and new user account" {
                var result = service.execute(null, userAccount, HashMap())
                result.result shouldBe SOAResultType.SUCCESS
                result.data shouldBe userAccount
            }
        }

        "!calling execute with an invalid user account" should {
            "return a fail result" {
                val userData = userAccount.userMetadata._value as User
                userData.email = "BADEMAIL"
                var result = service.execute(null, userAccount, HashMap())
                result.result shouldBe SOAResultType.FAILURE
                result.message shouldBe "Validation failed: email"
            }
        }

        "!calling execute with an already existing user account" should {
            "return a fail result" {
                var result = service.execute(null, userAccount, HashMap())
                result.result shouldBe SOAResultType.SUCCESS
                result.data shouldBe userAccount
                var result2 = service.execute(null, userAccount, HashMap())
                result2.result shouldBe SOAResultType.FAILURE
                result2.message shouldBe "That username or email already exists"
            }
        }
    }
}