package main.userAccounts.models

import kotlinserverless.framework.models.BaseModel
import org.joda.time.DateTime

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
data class UserAccount(
        override var id: Int?,
        var userMetadata: UserMetadata,
        var cryptoKeyPair: CryptoKeyPair,
        var apiCreds: ApiCreds
) : BaseModel()

/**
 * User information
 * @property email
 * @property firstName
 * @property lastName
 * @property userName
 */
data class UserMetadata(
        var email: String,
        var firstName: String,
        var lastName: String,
        var userName: String
)

/**
 * Api credentials
 * @property publicKey
 * @property encryptedPrivateKey
 */
data class CryptoKeyPair(
        var publicKey: String,
        var encryptedPrivateKey: String
)

/**
 * Api credentials
 * @property apiKey
 * @property encryptedSecretKey
 */
data class ApiCreds(
        var apiKey: String,
        var encryptedSecretKey: String
)

/**
 * Session Information
 * @property sessionKey
 * @property expiration
 */
data class SessionInformation(
        var sessionKey: String,
        var expiration: DateTime
)