package main.services.token

import framework.services.DaoService
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.Token
import main.daos.TokenNamespace
import main.daos.TokenTypes
import main.daos.Tokens
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.insertAndGetId

/**
 * Generate a token if it is valid
 */
class GenerateTokenService: SOAServiceInterface<Token> {
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

            return@execute Token.findById(tokenId!!)!!
        }
    }
}