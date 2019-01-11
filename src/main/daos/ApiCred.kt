package main.daos

import framework.models.BaseIntEntity
import framework.models.BaseIntEntityClass
import framework.models.BaseIntIdTable
import main.helpers.EncryptionHelper
import org.jetbrains.exposed.dao.EntityID

/**
 * Api credentials
 * @property apiKey
 * @property secretKey
 */
class ApiCred(id: EntityID<Int>) : BaseIntEntity(id, ApiCreds) {
    companion object : BaseIntEntityClass<ApiCred>(ApiCreds)

    var apiKey by ApiCreds.apiKey
    var _secretKey by ApiCreds.secretKey
    var secretKey : String
        get() = _secretKey
        set(value) {
            val encryption = EncryptionHelper.encrypt(value)
            _secretKey = encryption.first
            _secretKeySalt = encryption.second
        }
    var _secretKeySalt by ApiCreds.secretKeySalt

    override fun toMap(): MutableMap<String, Any?> {
        var map = super.toMap()
        map.put("apiKey", apiKey)
        return map
    }
}

object ApiCreds : BaseIntIdTable("api_creds") {
    val apiKey = varchar("api_key", 256)
    // TODO: look into how this can be done better
    val secretKey = varchar("secretKey", 256)
    val secretKeySalt = varchar("secretKeySalt", 256)
}

data class ApiCredNamespace(val apiKey: String, val secretKey: String = "")