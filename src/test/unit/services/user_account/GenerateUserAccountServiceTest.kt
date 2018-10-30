package test.unit.services.user_account

import io.kotlintest.*
import io.kotlintest.matchers.string.shouldContain
import io.kotlintest.specs.WordSpec
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import main.services.user_account.GenerateUserAccountService
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

@ExtendWith(MockKExtension::class)
class GenerateUserAccountServiceTest : WordSpec() {
    private var service = GenerateUserAccountService()
    lateinit private var params: MutableMap<String, String>
    lateinit private var connection: Connection

    override fun beforeTest(description: Description): Unit {
        val database = Database.connect("jdbc:h2:mem:test;MODE=MySQL", driver = "org.h2.Driver")
        transaction {
            SchemaUtils.create(Users, CryptoKeyPairs, ApiCreds, Sessions, UserAccounts)
        }
        params = mutableMapOf(
            Pair("email", "dev@ncnt.io"),
            Pair("firstname", "dev"),
            Pair("lastname", "ncnt")
        )
        connection = database.connector.invoke()
    }

    override fun afterTest(description: Description, result: TestResult) {
        transaction {
            SchemaUtils.drop(Users, CryptoKeyPairs, ApiCreds, Sessions, UserAccounts)
        }
        connection.close()
    }

    init {
        "calling execute with a valid user account" should {
            "return a success result and new user account" {
                var result = service.execute(null, params)
                result.result shouldBe SOAResultType.SUCCESS
            }
        }

        "calling execute with an invalid user account" should {
            "return a fail result" {
                params.put("email", "BADEMAIL")
                var result = service.execute(null, params)
                result.result shouldBe SOAResultType.FAILURE
                result.message.shouldContain("Check constraint violation")
            }
        }

        "calling execute with an already existing user account" should {
            "return a fail result" {
                var result = service.execute(null, params)
                result.result shouldBe SOAResultType.SUCCESS
                var result2 = service.execute(null, params)
                result2.result shouldBe SOAResultType.FAILURE
                result2.message.shouldContain("Unique index or primary key violation")
            }
        }
    }
}