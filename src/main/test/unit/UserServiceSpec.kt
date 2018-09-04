package kotlinserverless.main.test.unit

import kotlinserverless.main.users.services.UserService
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.junit.jupiter.api.Assertions.assertEquals

object UserServiceSpec: Spek({
    given("a User Service") {
		var userService = UserService()
       
        on("calling hello") {
            it("should return HELLO WORLD") {
                assertEquals("HELLO WORLD", userService.hello())
            }
        }
    }
})