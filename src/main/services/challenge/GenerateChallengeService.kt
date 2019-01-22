package main.services.challenge

import framework.models.idValue
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import main.services.completion_criteria.GenerateCompletionCriteriaService
import main.services.reward.GenerateRewardService
import main.services.transaction.GenerateTransactionService
import main.services.user_account.GenerateCryptoKeyPairService
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.SizedCollection

/**
 * Create a challenge; generate all appropriate objects including transaction(s)
 */
object GenerateChallengeService {
    fun execute(caller: UserAccount, challengeNamespace: ChallengeNamespace) : SOAResult<Challenge> {
        val userAccount = UserAccount.findById(challengeNamespace.challengeSettings.admin)!!
        val settings = GenerateChallengeSettingsService.execute(userAccount, challengeNamespace.challengeSettings)
        if(settings.result != SOAResultType.SUCCESS)
            return SOAResult(SOAResultType.FAILURE, settings.message)

        val keyPairGenerated = GenerateCryptoKeyPairService.execute()
        if(keyPairGenerated.result != SOAResultType.SUCCESS)
            return SOAResult(SOAResultType.FAILURE, keyPairGenerated.message)

        val optionalParentChallenge = if(challengeNamespace.parentChallenge != null)
            Challenge.findById(challengeNamespace.parentChallenge.toInt())
        else
            null

        val distributionFeeRewardResult = GenerateRewardService.execute(challengeNamespace.distributionFeeReward)

        // TODO add reward to pool?
        val challenge = Challenge.new {
            parentChallenge = optionalParentChallenge
            challengeSettings = settings.data!!
            cryptoKeyPair = keyPairGenerated.data!!.value
            distributionFeeReward = distributionFeeRewardResult.data!!
        }

        challenge.completionCriterias = createCompletionCriteria(caller, challengeNamespace.completionCriteria)

        if(challengeNamespace.subChallenges!!.any()) {
            challenge.subChallenges = createSubChallengesList(challengeNamespace.subChallenges!!)
        }

        // create a transaction for challenge creation state
        val createChallengeTx = GenerateTransactionService.execute(TransactionNamespace(
            from = challenge.cryptoKeyPair.publicKey,
            to = challenge.cryptoKeyPair.publicKey,
            previousTransaction = null,
            metadatas = null,
            action = ActionNamespace(
                type = ActionType.CREATE,
                data = challenge.idValue,
                dataType = Challenge::class.simpleName!!
            )
        ))
        if(createChallengeTx.result != SOAResultType.SUCCESS)
            return SOAResult(SOAResultType.FAILURE, createChallengeTx.message)

        // create a transaction for tokens received?
        GenerateTransactionService.execute(TransactionNamespace(
            from = challenge.cryptoKeyPair.publicKey,
            to = userAccount.cryptoKeyPair.publicKey,
            previousTransaction = null,
            metadatas = ChallengeMetadata(
                            challenge.idValue,
                            challenge.challengeSettings.offChain,
                            challenge.challengeSettings.shareExpiration.toString(),
                            challenge.challengeSettings.maxShares
                        ).getChallengeMetadataNamespaces().toTypedArray(),
            action = ActionNamespace(
                type = ActionType.SHARE,
                data = challenge.idValue,
                dataType = Challenge::class.simpleName!!
            )
        ))

        return SOAResult(SOAResultType.SUCCESS, null, challenge)
    }

    private fun createSubChallengesList(subChallengeIds: List<SubChallengeNamespace>) : SizedCollection<SubChallenge> {
        var subChallenges = mutableListOf<SubChallenge>()
        subChallengeIds.forEach {
            subChallenges.add(SubChallenge.new {
                subChallenge = EntityID(it.subChallengeId, Challenges)
                type = SubChallengeType.valueOf(it.type!!)
            })
        }
        return SizedCollection(subChallenges)
    }

    private fun createCompletionCriteria(caller: UserAccount, completionCriteriaNamespace: CompletionCriteriaNamespace) : CompletionCriteria {
        // TODO think about how we handle generating completion criteria
        // TODO should this happen AFTER a challenge is created?
        // TODO how and when will the pool be populated

        return GenerateCompletionCriteriaService.execute(caller, completionCriteriaNamespace).data!!
    }
}