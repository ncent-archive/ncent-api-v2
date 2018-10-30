package test.unit.daos

import framework.models.idValue
import io.kotlintest.specs.WordSpec
import io.kotlintest.Description
import io.kotlintest.TestResult
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import kotlinserverless.framework.models.Handler
import main.daos.User
import main.daos.Users
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction


@ExtendWith(MockKExtension::class)
class UserDaoTest : WordSpec() {
    lateinit var user: User

    override fun beforeTest(description: Description): Unit {
        Handler.connectToDatabase()
        transaction {
            // TODO figure out why i need to drop here and not in the afterTest
            SchemaUtils.drop(Users)
            SchemaUtils.create(Users)
        }
        user = transaction {
            return@transaction User.new {
                email = "test@email.com"
                firstname = "defaultFirstName"
                lastname = "defaultLastName"
            }
        }
    }

    override fun afterTest(description: Description, result: TestResult) {
        transaction {
            SchemaUtils.drop(Users)
        }
        Handler.disconnectFromDatabase()
    }

    init {
        "creating a user" should {
            "return a user with id" {
                user.idValue shouldNotBe null
                user.email shouldBe "test@email.com"
                user.firstname shouldBe "defaultFirstName"
                user.lastname shouldBe "defaultLastName"
            }
            "be queryable" {
                val queryUser = transaction {
                    return@transaction User.all().first()
                }
                queryUser.idValue shouldNotBe null
                queryUser.email shouldBe "test@email.com"
                queryUser.firstname shouldBe "defaultFirstName"
                queryUser.lastname shouldBe "defaultLastName"
            }
            "not be queryable for a non-existent user" {
                val queryUserNonExists = transaction {
                    return@transaction User.find({ Users.id eq 100 }).empty()
                }
                queryUserNonExists shouldBe true
            }
        }
    }
}
