package main.daos

import framework.models.BaseIntEntity
import framework.models.BaseIntEntityClass
import framework.models.BaseIntIdTable
import framework.models.BaseObject
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.sql.ReferenceOption

/**
 * User Accounts will be used exclusively by the API in order to manage
 * user usage of the API. This will handle storage of the user information,
 * their wallet information, and the api information. Services will include:
 * generation, validation, and throttling.
 * @property id
 * @property userMetadata UserMetadata
 * @property cryptoKeyPair CryptoKeyPair
 * @property apiCreds ApiCreds
 */
class UserAccount(id: EntityID<Int>) : BaseIntEntity(id, UserAccounts) {
    companion object : BaseIntEntityClass<UserAccount>(UserAccounts)

    var userMetadata by User referencedOn UserAccounts.userMetadata
    var cryptoKeyPair by CryptoKeyPair referencedOn UserAccounts.cryptoKeyPair
    var apiCreds by ApiCred referencedOn UserAccounts.apiCreds

    override fun toMap(): MutableMap<String, Any?> {
        var map = super.toMap()
        map.put("userMetadata", userMetadata.toMap())
        map.put("cryptoKeyPair", cryptoKeyPair.toMap())
        map.put("apiCreds", apiCreds.toMap())
        return map
    }
}

object UserAccounts : BaseIntIdTable("user_accounts") {
    val userMetadata = reference("user", Users, onDelete = ReferenceOption.CASCADE)
    val cryptoKeyPair = reference("crypto_key_pair", CryptoKeyPairs)
    val apiCreds = reference("api_cred", ApiCreds)
}

data class UserAccountNamespace(val userMetadata: UserNamespace, val cryptoKeyPair: CryptoKeyPairNamespace, val apiCreds: ApiCredNamespace)

data class NewUserAccountNamespace(val value: UserAccountNamespace, val privateKey: String, val secretKey: String)

class NewUserAccount(
        val value: UserAccount,
        val privateKey: String,
        val secretKey: String
): BaseObject {
    override fun toMap(): MutableMap<String, Any?> {
        val map = mutableMapOf<String, Any?>()
        map.put("value", value.toMap())
        map.put("privateKey", privateKey)
        map.put("secretKey", secretKey)
        return map
    }
}
