package main.daos

import framework.models.BaseIntEntity
import framework.models.BaseIntEntityClass
import framework.models.BaseIntIdTable
import org.jetbrains.exposed.dao.EntityID

/**
 * Api credentials
 * @property publicKey
 * @property encryptedPrivateKey
 */
class CryptoKeyPair(id: EntityID<Int>) : BaseIntEntity(id, CryptoKeyPairs) {
    companion object : BaseIntEntityClass<CryptoKeyPair>(CryptoKeyPairs) {
        fun encryptPrivateKey(privateKey: String) : String {
            // TODO figure out encryption alg here
            return privateKey
        }
    }

    var publicKey by CryptoKeyPairs.publicKey
    var encryptedPrivateKey by CryptoKeyPairs.encryptedPrivateKey
}

object CryptoKeyPairs : BaseIntIdTable("crypto_key_pairs") {
    val publicKey = varchar("public_key", 256)
    // TODO: look into how this can be done better
    val encryptedPrivateKey = varchar("encrypted_private_key", 256)
}

data class CryptoKeyPairNamespace(
    val publicKey: String,
    val encryptedPrivateKey: String
)