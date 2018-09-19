package kotlinserverless.test

import com.amazonaws.services.lambda.runtime.Context
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.whenever
import kotlinserverless.framework.dispatchers.RequestDispatcher
import kotlinserverless.framework.models.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import org.mockito.Mockito
import kotlin.test.assertEquals

object HandlerSpec: Spek({
    describe("a new event") {
        val handler by memoized { Handler() }
        val context by memoized { Mockito.mock(Context::class.java) }
        val map: MutableMap<String, Any> = mutableMapOf()
        val dispatcher by memoized { Mockito.mock(RequestDispatcher::class.java) }

        beforeEach {
            handler?.requestDispatcher = dispatcher!!
            map["path"] = "test"
        }

        describe("correct path") {
            it("should return a status code of 204 if the response body is empty") {
                whenever(dispatcher?.locate(any())).thenReturn(EmptyModel())
                val response = context?.let { handler?.handleRequest(map as Map<String, Any>, it) }
                assertEquals(204, response?.statusCode)
            }
            it("should return a status code of 200 if the response body is not empty") {
                whenever(dispatcher?.locate(any())).thenReturn(TestModel())
                val response = context?.let { handler?.handleRequest(map as Map<String, Any>, it) }
                assertEquals(200, response?.statusCode)
            }

        }
        describe("non-existent path") {
            it("should return a status code of 404") {
                whenever(dispatcher?.locate(any())).thenThrow(RouterException(""))
                val response = context?.let { handler?.handleRequest(map as Map<String, Any>, it) }
                assertEquals(404, response?.statusCode)
            }
        }
    }
})