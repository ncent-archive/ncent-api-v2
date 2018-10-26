package main.daos

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

    var userMetadata by UserAccounts.userMetadata
    var cryptoKeyPair by UserAccounts.cryptoKeyPair
    var apiCreds by UserAccounts.apiCreds
    var session by UserAccounts.session
}

object UserAccounts : BaseIntIdTable("user_accounts") {
    val userMetadata = reference("user", Users)
    val cryptoKeyPair = reference("crypto_key_pair", CryptoKeyPairs)
    val apiCreds = reference("api_cred", ApiCreds)
    val session = reference("session", Sessions)
}