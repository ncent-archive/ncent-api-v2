package test.unit.services

import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.main.services.users.UserHelloService

@ExtendWith(MockKExtension::class)
class UserHelloServiceTest : WordSpec() {
    private var service = UserHelloService()

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
