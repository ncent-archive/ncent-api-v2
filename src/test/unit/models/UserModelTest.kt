package test.unit.models

import framework.models.idValue
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import io.kotlintest.Description
import io.kotlintest.shouldNotBe
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.junit.jupiter.api.extension.ExtendWith
import kotlinserverless.framework.models.Handler
import kotlinserverless.main.users.models.User
import kotlinserverless.main.users.models.Users
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

@ExtendWith(MockKExtension::class)
class UserModelTest : WordSpec() {
    override fun beforeTest(description: Description): Unit {
        Handler(mockk()).connectToDatabase()
    }

    init {
        "creating a user" should {
            "return a user with id" {
                val user = transaction {
                    SchemaUtils.create(Users)
                    return@transaction User.new {
                        email = "test@email.com"
                        firstname = "defaultFirstName"
                        lastname = "defaultLastName"
                    }
                }
                user.idValue shouldNotBe null
                user.email shouldBe "test@email.com"
                user.firstname shouldBe "defaultFirstName"
                user.lastname shouldBe "defaultLastName"
            }
        }
    }
}
