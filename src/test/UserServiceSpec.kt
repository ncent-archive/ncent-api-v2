package kotlinserverless.test

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals
import kotlinserverless.main.users.services.UserService

object UserServiceSpec: Spek({
    describe("a User Service") {
		val userService by memoized { UserService() }

        describe("calling hello") {
            it("should return HELLO WORLD") {
                assertEquals("HELLO WORLD", userService.hello())
            }
        }
    }
})