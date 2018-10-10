package test.unit.services

import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import io.kotlintest.Description
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import io.mockk.mockk
import kotlinserverless.framework.models.*
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.main.users.models.User
import kotlinserverless.main.users.services.UserHelloService

@ExtendWith(MockKExtension::class)
class UserHelloServiceTest : WordSpec() {
    private lateinit var service: UserHelloService
    private lateinit var user: User
    private lateinit var request: Request

    override fun beforeTest(description: Description): Unit {
        service = UserHelloService()
        user = mockk(relaxed = true)
        request = mockk()
    }

    init {
        "calling hello on a User Service" should {
            "return HELLO WORLD" {
                var result = service.execute(123, "HELLO", HashMap())
                result.result shouldBe SOAResultType.SUCCESS
                result.data shouldBe "HELLO WORLD"
            }
        }
    }
}
