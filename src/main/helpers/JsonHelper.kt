package main.helpers

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon


// Used to instantiate klaxon once
// Also allowing a clean call via ex:
// -- val newUserAccount: NewUserAccountNamespace = JsonHelper.parse(response.body!!)!!
object JsonHelper {
    val KLAX = Klaxon()

    inline fun <reified T> parse(value: String): T {
        return KLAX.parse<T>(value)!!
    }

    inline fun <reified T> parse(value: Map<String, Any?>): T {
        val string = KLAX.toJsonString(value)
        return KLAX.parse<T>(string)!!
    }

    inline fun <reified T> parse(value: JsonObject): T {
        return KLAX.parseFromJsonObject<T>(value)!!
    }

    inline fun <reified T> parseArray(value: String): List<T> {
        return KLAX.parseArray(value)!!
    }
}