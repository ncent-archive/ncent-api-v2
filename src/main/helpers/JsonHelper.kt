package main.helpers

import com.beust.klaxon.Klaxon


// Used to instantiate klaxon once
// Also allowing a clean call via ex:
// -- val newUserAccount: NewUserAccountNamespace = JsonHelper.parse(response.body!!)!!
object JsonHelper {
    val KLAX = Klaxon()

    inline fun <reified T> parse(value: String): T {
        return KLAX.parse<T>(value)!!
    }
}