package test.unit.services

import framework.services.DaoService
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import io.kotlintest.Description
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.junit.jupiter.api.extension.ExtendWith
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.models.Handler

@ExtendWith(MockKExtension::class)
class DaoServiceTest : WordSpec() {
    private lateinit var service: DaoService<String>

    override fun beforeTest(description: Description): Unit {
        service = DaoService()
        Handler.connectToDatabase()
    }

    init {
        "calling execute on a dao service" should {
            "execute the passed function" {
                var result = service.execute({ "HELLO WORLD" })
                result.result shouldBe SOAResultType.SUCCESS
                result.data shouldBe "HELLO WORLD"
            }
        }
    }
}
