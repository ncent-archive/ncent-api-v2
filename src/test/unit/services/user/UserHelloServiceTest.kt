package test.unit.services.user

import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.main.services.users.UserHelloService

@ExtendWith(MockKExtension::class)
class UserHelloServiceTest : WordSpec() {
    init {
        "calling hello on a User Service" should {
            "return HELLO WORLD" {
                var result = UserHelloService.execute()
                result.result shouldBe SOAResultType.SUCCESS
                result.data shouldBe "HELLO WORLD"
            }
        }
    }
}
