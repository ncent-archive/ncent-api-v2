package main.services.challenge

import framework.services.DaoService
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.*
import main.services.transaction.GetTransactionsService
import org.jetbrains.exposed.sql.alias
import org.jetbrains.exposed.sql.innerJoin
import org.jetbrains.exposed.sql.leftJoin
import org.jetbrains.exposed.sql.select
import javax.ws.rs.NotFoundException

/**
 *
 * Retrieve one or more challenges based on filters
 *
 */
object GetChallengesService: SOAServiceInterface<ChallengeList> {
    // get challenges for a caller
    override fun execute(caller: Int?): SOAResult<ChallengeList> {
        val challengeResult = DaoService.execute {
            val publicKey = UserAccount.findById(caller!!)!!.cryptoKeyPair.publicKey
            val transactionResult = GetTransactionsService.execute(
                caller,
                mapOf(
                    Pair("to", publicKey),
                    Pair("type", "SHARE"),
                    Pair("dataType", "Challenge")
                )
            )

            if(transactionResult.result != SOAResultType.SUCCESS)
                throw Exception(transactionResult.message)

            val challengeIds = transactionResult.data!!.transactions
                .map { tx -> tx.metadatas }.flatten()
                .filter { md -> md.key == "challengeId" }
                .map { md -> md.value.toInt() }
                .distinct()

            if(!challengeIds.any())
                throw NotFoundException("No Challenges found for this user")

            val challengeToSubChallengeTable = ChallengeToSubChallenges.alias("challenge_to_sub_challenge")
            val subChallengeTable = SubChallenges.alias("sub_challenge")
            val query = Challenges
                    .leftJoin(challengeToSubChallengeTable, { Challenges.id }, { challengeToSubChallengeTable[ChallengeToSubChallenges.challenge] })
                    .leftJoin(subChallengeTable, { challengeToSubChallengeTable[ChallengeToSubChallenges.subChallenge] }, { subChallengeTable[SubChallenges.id] })
                    .select {
                        Challenges.id inList challengeIds
                    }.withDistinct()
            Challenge.wrapRows(query).toList().distinct()
        }

        if(challengeResult.result != SOAResultType.SUCCESS)
            return SOAResult(challengeResult.result, challengeResult.message, null)
        return SOAResult(SOAResultType.SUCCESS, null, ChallengeList(challengeResult.data!!))
    }
}