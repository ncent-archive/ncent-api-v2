package test.unit.daos

import framework.models.idValue
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import io.kotlintest.Description
import io.kotlintest.shouldNotBe
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.junit.jupiter.api.extension.ExtendWith
import kotlinserverless.framework.models.Handler
import kotlinserverless.main.daos.User
import kotlinserverless.main.daos.Users
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction


@ExtendWith(MockKExtension::class)
class UserDaoTest : WordSpec() {
    override fun beforeTest(description: Description): Unit {
        Handler(mockk()).connectToDatabase()
    }

    init {
        "creating a user" should {
            "return a user with id" {
                val user = generateUser()
                user.idValue shouldNotBe null
                user.email shouldBe "test@email.com"
                user.firstname shouldBe "defaultFirstName"
                user.lastname shouldBe "defaultLastName"
            }
            "be queryable" {
                val queryUser = transaction {
                    generateUser()
                    return@transaction User.all().first()
                }
                queryUser.idValue shouldNotBe null
                queryUser.email shouldBe "test@email.com"
                queryUser.firstname shouldBe "defaultFirstName"
                queryUser.lastname shouldBe "defaultLastName"
            }
            "not be queryable for a non-existent user" {
                val queryUserNonExists = transaction {
                    generateUser()
                    return@transaction User.find({ Users.id eq 100 }).empty()
                }
                queryUserNonExists shouldBe true
            }
        }
    }

    // TODO: might be able to do this better via some kind of sub before/after
    fun generateUser() : User {
        return transaction {
            SchemaUtils.create(Users)
            return@transaction User.new {
                email = "test@email.com"
                firstname = "defaultFirstName"
                lastname = "defaultLastName"
            }
        }
    }
}
