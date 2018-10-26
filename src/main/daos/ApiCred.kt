package main.daos

import framework.models.BaseIntEntity
import framework.models.BaseIntEntityClass
import framework.models.BaseIntIdTable
import org.jetbrains.exposed.dao.EntityID

/**
 * Api credentials
 * @property apiKey
 * @property encryptedSecretKey
 */
class ApiCred(id: EntityID<Int>) : BaseIntEntity(id, ApiCreds) {
    companion object : BaseIntEntityClass<ApiCred>(ApiCreds) {
        fun encryptSecretKey(secretKey: String) : String {
            // TODO figure out encryption alg here
            return secretKey
        }
    }

    var apiKey by ApiCreds.apiKey
    var encryptedSecretKey by ApiCreds.encryptedSecretKey
}

object ApiCreds : BaseIntIdTable("api_creds") {
    val apiKey = varchar("api_key", 256)
    // TODO: look into how this can be done better
    val encryptedSecretKey = varchar("encrypted_secret_key", 256)
}