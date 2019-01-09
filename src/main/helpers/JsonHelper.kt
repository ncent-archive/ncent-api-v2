package main.helpers

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import java.io.StringReader

object JsonHelper {
    private val KLAX = Klaxon()

    fun parse(jsonString: String, name: String? = null): JsonObject {
        var trimmedJsonString = jsonString.trim()
        return when {
            trimmedJsonString.startsWith("{") -> {
                var obj = KLAX.parseJsonObject(StringReader(jsonString))
                parseObj(obj)
            }
            trimmedJsonString.startsWith("[") -> {
                var obj = KLAX.parseJsonArray(StringReader(jsonString))
                var finalObj = JsonObject()
                finalObj[name!!] = parseArray(obj)
                finalObj
            }
            else -> {
                var obj = JsonObject()
                obj[name!!] = jsonString
                obj
            }
        }
    }

    private fun parseObj(obj: JsonObject): JsonObject {
        var finalObj = JsonObject()
        obj.forEach { k, value ->
            if(value is JsonObject) {
                finalObj[k] = parse(value.toJsonString())
            } else if(value is JsonArray<*>) {
                finalObj[k] = parseArray(value)
            } else {
                finalObj[k] = value!!
            }
        }
        return finalObj
    }

    private fun parseArray(array: JsonArray<*>): JsonArray<Any> {
        var finalArray = JsonArray<Any>()
        array.forEach { value ->
            if(value is JsonObject) {
                finalArray.add(parse(value.toJsonString()))
            } else if(value is JsonArray<*>) {
                finalArray.add(parseArray(value))
            } else {
                finalArray.add(value!!)
            }
        }
        return finalArray
    }
}