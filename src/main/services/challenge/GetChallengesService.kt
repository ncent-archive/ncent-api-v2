package main.services.challenge

import framework.models.idValue
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import main.services.transaction.GetTransactionsService
import org.jetbrains.exposed.sql.alias
import org.jetbrains.exposed.sql.leftJoin
import org.jetbrains.exposed.sql.select

/**
 *
 * Retrieve one or more challenges based on filters
 *
 */
// TODO maybe include how many shares available??
object GetChallengesService {
    // get challenges for a caller
    fun execute(caller: UserAccount): SOAResult<ChallengeToUnsharedTransactionsList> {
        val publicKey = caller.cryptoKeyPair.publicKey
        val transactionResult = GetTransactionsService.execute(
            from = null,
            to = publicKey,
            previousTxId = null,
            actionNamespace = ActionNamespace(type = ActionType.SHARE, data = null, dataType = "Challenge")
        )

        if(transactionResult.result != SOAResultType.SUCCESS)
            return SOAResult(SOAResultType.FAILURE, transactionResult.message)

        val challengeIds = transactionResult.data!!.transactions
            .map { tx -> tx.metadatas }.flatten()
            .filter { md -> md.key == "challengeId" }
            .map { md -> md.value.toInt() }
            .distinct()

        if(!challengeIds.any())
            return SOAResult(SOAResultType.FAILURE, "No Challenges found for this user")

        val challengeToSubChallengeTable = ChallengeToSubChallenges.alias("challenge_to_sub_challenge")
        val subChallengeTable = SubChallenges.alias("sub_challenge")
        val query = Challenges
            .leftJoin(challengeToSubChallengeTable, { Challenges.id }, { challengeToSubChallengeTable[ChallengeToSubChallenges.challenge] })
            .leftJoin(subChallengeTable, { challengeToSubChallengeTable[ChallengeToSubChallenges.subChallenge] }, { subChallengeTable[SubChallenges.id] })
            .select {
                Challenges.id inList challengeIds
            }.withDistinct()
        val challengeResult = Challenge.wrapRows(query).toList().distinct()

        var challengeToUnsharedTransactionsList = mutableListOf<ChallengeToUnsharedTransaction>()
        challengeResult.forEach {
            val sharesForChallenge = GetUnsharedTransactionsService.execute(
                caller,
                it.idValue
            ).data!!
            challengeToUnsharedTransactionsList.add(ChallengeToUnsharedTransaction(it, sharesForChallenge))
        }
        return SOAResult(SOAResultType.SUCCESS, null, ChallengeToUnsharedTransactionsList(challengeToUnsharedTransactionsList))
    }
}