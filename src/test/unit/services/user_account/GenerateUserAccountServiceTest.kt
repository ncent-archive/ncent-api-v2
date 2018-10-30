package test.unit.services.user_account

import io.kotlintest.*
import io.kotlintest.matchers.string.shouldContain
import io.kotlintest.specs.WordSpec
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import main.services.user_account.GenerateUserAccountService
import kotlinserverless.framework.models.Handler
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

@ExtendWith(MockKExtension::class)
class GenerateUserAccountServiceTest : WordSpec() {
    private var service = GenerateUserAccountService()
    lateinit private var params: MutableMap<String, String>

    override fun beforeTest(description: Description): Unit {
        Handler.connectToDatabase()
        transaction {
            SchemaUtils.create(Users, CryptoKeyPairs, ApiCreds, Sessions, UserAccounts)
        }
        params = mutableMapOf(
            Pair("email", "dev@ncnt.io"),
            Pair("firstname", "dev"),
            Pair("lastname", "ncnt")
        )
    }

    override fun afterTest(description: Description, result: TestResult) {
        transaction {
            SchemaUtils.drop(Users, CryptoKeyPairs, ApiCreds, Sessions, UserAccounts)
        }
        Handler.disconnectFromDatabase()
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