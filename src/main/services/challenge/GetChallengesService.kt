package main.services.challenge

import framework.services.DaoService
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.services.SOAServiceInterface
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
object GetChallengesService: SOAServiceInterface<ChallengeList> {
    // get challenges for a caller
    override fun execute(caller: Int?): SOAResult<ChallengeList> {
        val challengeResult = DaoService.execute {
            val publicKey = UserAccount.findById(caller!!)!!.cryptoKeyPair.publicKey
            val transactionResult = GetTransactionsService.execute(
                caller,
                mapOf(
                    Pair("to", publicKey),
                    Pair("dataType", "Challenge")
                )
            )

            val challengeIds = transactionResult.data!!.transactions
                .map { tx -> tx.metadatas }.flatten()
                .filter { md -> md.value == "challengeId" }
                .map { md -> md.value.toInt() }
                .distinct()

            if(transactionResult.result != SOAResultType.SUCCESS)
                throw Exception(transactionResult.message)

            val parentChallengeTable = Challenges.alias("parent")
            val challengeSettingTable = ChallengeSettings.alias("settings")
            val keyPairTable = CryptoKeyPairs.alias("keyPair")
            val distributionFeeRewardTable = Rewards.alias("feeReward")
            val query = Challenges
                    .innerJoin(SubChallenges)
                    .leftJoin(parentChallengeTable, { Challenges.id }, {parentChallengeTable[Challenges.parentChallenge]})
                    .leftJoin(challengeSettingTable, { ChallengeSettings.id }, {challengeSettingTable[Challenges.challengeSettings]})
                    .leftJoin(keyPairTable, { CryptoKeyPairs.id }, {keyPairTable[Challenges.cryptoKeyPair]})
                    .leftJoin(distributionFeeRewardTable, { Rewards.id }, {distributionFeeRewardTable[Challenges.distributionFeeReward]})
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