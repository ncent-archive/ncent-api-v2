package main.daos

import com.fasterxml.jackson.databind.ObjectMapper
import framework.models.BaseIntEntity
import framework.models.BaseIntEntityClass
import framework.models.BaseIntIdTable
import org.jetbrains.exposed.dao.*

/**
 * User Accounts will be used exclusively by the API in order to manage
 * user usage of the API. This will handle storage of the user information,
 * their wallet information, and the api information. Services will include:
 * generation, validation, and throttling.
 * @property id
 * @property userMetadata UserMetadata
 * @property cryptoKeyPair CryptoKeyPair
 * @property apiCreds ApiCreds
 * @property session Sessions
 */
class UserAccount(id: EntityID<Int>) : BaseIntEntity(id, UserAccounts) {
    companion object : BaseIntEntityClass<UserAccount>(UserAccounts)

    var userMetadata by User referencedOn UserAccounts.userMetadata
    var cryptoKeyPair by CryptoKeyPair referencedOn UserAccounts.cryptoKeyPair
    var apiCreds by ApiCred referencedOn UserAccounts.apiCreds
    var session by Session referencedOn UserAccounts.session

    override fun toMap(): MutableMap<String, Any?> {
        var map = super.toMap()
        map.put("userMetadata", userMetadata.toMap())
        map.put("cryptoKeyPair", cryptoKeyPair.toMap())
        map.put("apiCreds", apiCreds.toMap())
        map.put("session", session.toMap())
        return map
    }
}

object UserAccounts : BaseIntIdTable("user_accounts") {
    val userMetadata = reference("user", Users)
    val cryptoKeyPair = reference("crypto_key_pair", CryptoKeyPairs)
    val apiCreds = reference("api_cred", ApiCreds)
    val session = reference("session", Sessions)
}

data class NewUserAccount(val value: UserAccount, val privateKey: String, val secretKey: String) {
    override fun toString(): String {
        return ObjectMapper().writeValueAsString(mapOf(
            Pair("value", value.toString()),
            Pair("privateKey", privateKey),
            Pair("secretKey", secretKey)
        ))
    }
}
