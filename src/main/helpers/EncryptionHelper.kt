package main.helpers

import org.mindrot.jbcrypt.BCrypt

object EncryptionHelper {
    fun encrypt(
        value: String,
        // TODO -- BCRYPTSALTROUNDS env var should always be set to 12 or more in prod!!
        salt: String = BCrypt.gensalt(
            System.getenv("BCRYPTSALTROUNDS")?.toInt() ?: 4
        )
    ): Pair<String, String> {
        return Pair(BCrypt.hashpw(value, salt), salt)
    }

    fun validateEncryption(
        value: String,
        salt: String,
        encryptedValue: String
    ): Boolean {
        return encrypt(value, salt).first == encryptedValue
    }
}