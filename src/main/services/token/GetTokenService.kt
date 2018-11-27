package main.services.token

import framework.services.DaoService
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.Tokens
import main.daos.TokenTypes
import main.daos.Token
import org.jetbrains.exposed.sql.alias
import org.jetbrains.exposed.sql.leftJoin
import org.jetbrains.exposed.sql.select

/**
 * Retrieve tokens based on a filtered type (value being the TokenType)
 */
class GetTokenService: SOAServiceInterface<Token> {
    private val daoService = DaoService<List<Token>>()

    override fun execute(caller: Int?, key: String?) : SOAResult<Token> {
        val tokensResult = daoService.execute {
            val parentTokenTypeTable = TokenTypes.alias("parent_token_type")
            val query = Tokens
                .innerJoin(TokenTypes)
                .leftJoin(parentTokenTypeTable, { TokenTypes.id }, {parentTokenTypeTable[TokenTypes.parentToken]})
                .select {
                    TokenTypes.name eq key!!
                }.withDistinct()
            Token.wrapRows(query).toList().distinct()
        }

        if(tokensResult.result != SOAResultType.SUCCESS)
            return SOAResult(tokensResult.result, tokensResult.message, null)
        return SOAResult(SOAResultType.SUCCESS, null, tokensResult.data!!.first())
    }
}