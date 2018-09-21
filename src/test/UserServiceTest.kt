package test

import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import io.kotlintest.Description
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import io.mockk.mockk
import kotlinserverless.framework.models.*
import kotlinserverless.main.users.services.UserService

@ExtendWith(MockKExtension::class)
class UserServiceTest : WordSpec() {
    private lateinit var service: UserService
    private lateinit var user: ApiUser
    private lateinit var request: Request

    override fun beforeTest(description: Description): Unit {
        service = UserService()
        user = mockk()
        request = mockk()
    }

    init {
        "calling hello on a User Service" should {
            "return HELLO WORLD" {
                service.hello(user, request) shouldBe "HELLO WORLD"
            }
        }
    }
}
