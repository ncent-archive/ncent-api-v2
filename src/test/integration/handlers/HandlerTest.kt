package kotlinserverless.test.integration.handlers

import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import io.kotlintest.Description
import com.amazonaws.services.lambda.runtime.Context
import kotlinserverless.framework.dispatchers.RequestDispatcher
import kotlinserverless.framework.models.*
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import io.mockk.every
import io.mockk.mockk

@ExtendWith(MockKExtension::class)
class HandlerTest : WordSpec() {
    private lateinit var handler: Handler
    private lateinit var contxt: Context
    private lateinit var dispatcher: RequestDispatcher
    private val map = mapOf("path" to "/health/health")

    override fun beforeTest(description: Description): Unit {
        handler = Handler(true)
        contxt = mockk()
        dispatcher = mockk()
        handler.requestDispatcher = dispatcher
    }

    init {
        "correct path" should {
            "should return a status code of 204 if the response body is empty" {
                every { dispatcher.locate(any()) } returns null
                val response = contxt.let { handler.handleRequest(map, it) }
                response.statusCode shouldBe 204
            }
            "should return a status code of 200 if the response body is not empty" {
                every { dispatcher.locate(any()) } returns "Hello"
                val response = contxt.let { handler.handleRequest(map, it) }
                response.statusCode shouldBe 200
            }
        }
        "non-existent path" should {
            "should return a status code of 404" {
                every { dispatcher.locate(any()) } throws RouterException("")
                val response = contxt.let { handler.handleRequest(map, it) }
                response.statusCode shouldBe 404
            }
        }
    }
}
