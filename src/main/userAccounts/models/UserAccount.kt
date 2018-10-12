package main.userAccounts.models

import framework.models.BaseIntEntity
import framework.models.BaseIntEntityClass
import framework.models.BaseIntIdTable
import kotlinserverless.main.users.models.Users
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

/**
 * Api credentials
 * @property publicKey
 * @property encryptedPrivateKey
 */
class CryptoKeyPair(id: EntityID<Int>) : BaseIntEntity(id, CryptoKeyPairs) {
    companion object : BaseIntEntityClass<CryptoKeyPair>(CryptoKeyPairs)

    var publicKey by CryptoKeyPairs.publicKey
    var encryptedPrivateKey by CryptoKeyPairs.encryptedPrivateKey
}

object CryptoKeyPairs : BaseIntIdTable("crypto_key_pairs") {
    val publicKey = varchar("public_key", 256)
    // TODO: look into how this can be done better
    val encryptedPrivateKey = varchar("encrypted_private_key", 256)
}

/**
 * Api credentials
 * @property apiKey
 * @property encryptedSecretKey
 */
class ApiCred(id: EntityID<Int>) : BaseIntEntity(id, ApiCreds) {
    companion object : BaseIntEntityClass<ApiCred>(ApiCreds)

    var apiKey by ApiCreds.apiKey
    var encryptedSecretKey by ApiCreds.encryptedSecretKey
}

object ApiCreds : BaseIntIdTable("api_creds") {
    val apiKey = varchar("api_key", 256)
    // TODO: look into how this can be done better
    val encryptedSecretKey = varchar("encrypted_secret_key", 256)
}

/**
 * Session Information
 * @property sessionKey
 * @property expiration
 */
class Session(id: EntityID<Int>) : BaseIntEntity(id, Sessions) {
    companion object : BaseIntEntityClass<Session>(Sessions)

    var sessionKey by Sessions.sessionKey
    var expiration by Sessions.expiration
}

object Sessions : BaseIntIdTable("sessions") {
    val sessionKey = varchar("session_key", 256)
    // TODO: look into how this can be done better
    val expiration = datetime("expiration")
}