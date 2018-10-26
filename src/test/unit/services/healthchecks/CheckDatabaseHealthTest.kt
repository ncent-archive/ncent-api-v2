package test.unit.services.healthchecks

import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import io.kotlintest.Description
import io.mockk.every
import io.mockk.junit5.MockKExtension
import kotlinserverless.framework.models.Handler
import org.junit.jupiter.api.extension.ExtendWith
import kotlinserverless.framework.services.SOAResultType
import main.services.healthchecks.CheckDatabaseHealthService

@ExtendWith(MockKExtension::class)
class CheckDatabaseHealthTest : WordSpec() {
    init {
        "calling execute on a Database Health Check Service" should {
            "return healthy if the database connection works" {
                var result = CheckDatabaseHealthService.execute()
                result.result shouldBe SOAResultType.SUCCESS
                result.message shouldBe "Successfully connected to database"
                result.data shouldBe true
            }
            //TODO figure out why this mockk is failing
//            "return unhealthy if the database connection does not work" {
//                //every { Handler.connectToDatabase() } throws Exception("error")
//                var result = service.execute()
//                result.result shouldBe SOAResultType.SUCCESS
//                result.message shouldBe "Failed to connect to database: error"
//                result.data shouldBe false
//            }
        }
    }
}
