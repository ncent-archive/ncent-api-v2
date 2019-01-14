package main.daos

import com.fasterxml.jackson.databind.ObjectMapper
import framework.models.BaseIntEntity
import framework.models.BaseIntEntityClass
import framework.models.BaseIntIdTable
import main.helpers.EncryptionHelper
import org.jetbrains.exposed.dao.EntityID

/**
 * Api credentials
 * @property publicKey
 * @property privateKey
 */
class CryptoKeyPair(id: EntityID<Int>) : BaseIntEntity(id, CryptoKeyPairs) {
    companion object : BaseIntEntityClass<CryptoKeyPair>(CryptoKeyPairs)

    var publicKey by CryptoKeyPairs.publicKey
    var _privateKey by CryptoKeyPairs.privateKey
    var privateKey : String
        get() = _privateKey
        set(value) {
            val encryption = EncryptionHelper.encrypt(value)
            _privateKey = encryption.first
            _privateKeySalt = encryption.second
        }
    var _privateKeySalt by CryptoKeyPairs.privateKeySalt

    override fun toMap(): MutableMap<String, Any?> {
        var map = super.toMap()
        map.put("publicKey", publicKey)
        return map
    }
}

object CryptoKeyPairs : BaseIntIdTable("crypto_key_pairs") {
    val publicKey = varchar("public_key", 256)
    val privateKey = varchar("privateKey", 256)
    val privateKeySalt = varchar("privateKeySalt", 256)
}

data class CryptoKeyPairNamespace(
    val publicKey: String,
    val privateKey: String = ""
)

data class NewCryptoKeyPair(
    val value: CryptoKeyPair,
    val secret: String
)