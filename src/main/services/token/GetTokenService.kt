package main.services.token

import framework.services.DaoService
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.Tokens
import main.daos.TokenTypes
import main.daos.Token
import org.jetbrains.exposed.sql.select

/**
 * Retrieve tokens based on a filtered type (value being the TokenType)
 */
object GetTokenService: SOAServiceInterface<Token> {
    override fun execute(key: String) : SOAResult<Token> {
        val tokensResult = DaoService.execute {
            val query = Tokens
                .innerJoin(TokenTypes)
                .select {
                    TokenTypes.name eq key
                }.withDistinct()
            Token.wrapRows(query).toList().distinct()
        }

        if(tokensResult.result != SOAResultType.SUCCESS)
            return SOAResult(tokensResult.result, tokensResult.message, null)
        return SOAResult(SOAResultType.SUCCESS, null, tokensResult.data!!.first())
    }
}