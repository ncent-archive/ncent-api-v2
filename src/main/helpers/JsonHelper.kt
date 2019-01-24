package main.helpers

import com.beust.klaxon.Klaxon


// Used to instantiate klaxon once
// Also allowing a clean call via ex:
// -- val newUserAccount: NewUserAccountNamespace = JsonHelper.parse(response.body!!)!!
object JsonHelper {
    val KLAX = Klaxon()

    inline fun <reified T> parse(value: Any): T {
        return when (value) {
            is String -> parseString<T>(value)!!
            is Map<*,*> -> parseMap(value as Map<String, Any?>)
            else -> throw InternalError("There was a problem parsing to json: $value")
        }
    }

    inline fun <reified T> parseString(value: String): T {
        return KLAX.parse<T>(value)!!
    }

    inline fun <reified T> parseMap(value: Map<String, Any?>): T {
        val string = KLAX.toJsonString(value)
        return KLAX.parse<T>(string)!!
    }

    inline fun <reified T> parseArray(value: Any): List<T> {
        return when (value) {
            is String -> parseStringArray(value)
            is List<*> -> parseListArray(value as List<Any?>)
            else -> throw InternalError("There was a problem parsing to json: $value")
        }
    }

    inline fun <reified T> parseStringArray(value: String): List<T> {
        return KLAX.parseArray(value)!!
    }

    inline fun <reified T> parseListArray(value: List<*>): List<T> {
        return KLAX.parseArray(KLAX.toJsonString(value))!!
    }

}