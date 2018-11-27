package main.services.token

import framework.models.idValue
import framework.services.DaoService
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.*
import main.services.transaction.GenerateTransactionService
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.insertAndGetId
import java.lang.RuntimeException

/**
 * Generate a token if it is valid
 */
class GenerateTokenService: SOAServiceInterface<Token> {
    private val generateTransactioNservice = GenerateTransactionService()

    override fun execute(caller: Int?, d: Any?, params: Map<String, String>?) : SOAResult<Token> {
        val tokenNamespace = d!! as TokenNamespace
        return DaoService<Token>().execute {
            // TODO -- should generate a transaction
            val tokenTypeObjId = if(tokenNamespace.tokenType.parentToken != null) {
                TokenTypes.insertAndGetId {
                    it[name] = tokenNamespace.tokenType.name
                    it[parentToken] = EntityID(tokenNamespace.tokenType.parentToken!!, TokenTypes)
                    it[parentTokenConversionRate] = tokenNamespace.tokenType.parentTokenConversionRate
                }
            } else {
                TokenTypes.insertAndGetId {
                    it[name] = tokenNamespace.tokenType.name
                }
            }

            val tokenId = Tokens.insertAndGetId {
                it[amount] = tokenNamespace.amount
                it[tokenType] = EntityID(tokenTypeObjId!!.value, TokenTypes)
            }

            // TODO -- add a test for this!
            if(caller != null) {
                val userAccount = UserAccount.findById(caller)
                val result = generateTransactioNservice.execute(
                    userAccount!!.idValue,
                    TransactionNamespace(
                        "DEFAULT",
                        userAccount.cryptoKeyPair.publicKey,
                        ActionNamespace(
                            ActionType.TRANSFER,
                            tokenId.value,
                            Token::class.simpleName!!
                        ),
                        null,
                        MetadatasListNamespace(listOf(MetadatasNamespace("amount", tokenNamespace.amount.toString())))
                    ), null
                )
                if(result.result != SOAResultType.SUCCESS)
                    throw RuntimeException("Failed to generate a transaction funding creator")
            }

            return@execute Token.findById(tokenId.value)!!
        }
    }
}