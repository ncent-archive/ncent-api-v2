package main.userAccounts.models

import kotlinserverless.framework.models.BaseModel

/**
 * User Accounts will be used exclusively by the API in order to manage
 * user usage of the API. This will handle storage of the user information,
 * their wallet information, and the api information. Services will include:
 * generation, validation, and throttling.
 * @property id
 * @property email
 * @property firstName
 * @property lastName
 * @property userName
 * @property publicKey
 * @property encryptedPrivateKey
 * @property apiKey
 * @property encryptedSecretKey
 */
data class UserAccount(
        override var id: Int?,
        var email: String,
        var firstName: String,
        var lastName: String,
        var userName: String,
        var publicKey: String,
        var encryptedPrivateKey: String,
        var apiKey: String,
        var encryptedSecretKey: String
) : BaseModel()