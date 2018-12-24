package main.services.challenge

import framework.models.idValue
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.services.SOAServiceInterface
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
object GenerateChallengeService: SOAServiceInterface<Challenge> {
    override fun execute(caller: Int?, d: Any?, params: Map<String, String>?) : SOAResult<Challenge> {
        val challengeNamespace = d!! as ChallengeNamespace
        val userAccount = UserAccount.findById(challengeNamespace.challengeSettings.admin)!!
        val settings = ChallengeSetting.new {
            name = challengeNamespace.challengeSettings.name
            description = challengeNamespace.challengeSettings.description
            imageUrl = challengeNamespace.challengeSettings.imageUrl
            sponsorName = challengeNamespace.challengeSettings.sponsorName
            expiration = challengeNamespace.challengeSettings.expiration
            admin = userAccount.id
            offChain = challengeNamespace.challengeSettings.offChain
            maxShares = challengeNamespace.challengeSettings.maxShares
            maxRewards = challengeNamespace.challengeSettings.maxRewards
            maxDistributionFeeReward = challengeNamespace.challengeSettings.maxDistributionFeeReward
            maxSharesPerReceivedShare = challengeNamespace.challengeSettings.maxSharesPerReceivedShare
            maxDepth = challengeNamespace.challengeSettings.maxDepth
            maxNodes = challengeNamespace.challengeSettings.maxNodes
        }

        val keyPairGenerated = GenerateCryptoKeyPairService.execute()
        if(keyPairGenerated.result != SOAResultType.SUCCESS)
            return SOAResult(SOAResultType.FAILURE, keyPairGenerated.message)

        val optionalParentChallenge = if(challengeNamespace.parentChallenge != null)
            Challenge.findById(challengeNamespace.parentChallenge)
        else
            null

        val distributionFeeRewardResult = GenerateRewardService.execute(null, challengeNamespace.distributionFeeReward, null)

        // TODO add reward to pool?
        val challenge = Challenge.new {
            parentChallenge = optionalParentChallenge
            challengeSettings = settings
            cryptoKeyPair = keyPairGenerated.data!!.value
            distributionFeeReward = distributionFeeRewardResult.data!!
        }

        challenge.completionCriterias = createCompletionCriteria(challengeNamespace.completionCriteria)

        if(challengeNamespace.subChallenges.any()) {
            challenge.subChallenges = createSubChallengesList(challengeNamespace.subChallenges)
        }

        // create a transaction for challenge creation state
        val createChallengeTx = GenerateTransactionService.execute(caller, TransactionNamespace(
            from = challenge.cryptoKeyPair.publicKey,
            to = challenge.cryptoKeyPair.publicKey,
            previousTransaction = null,
            metadatas = null,
            action = ActionNamespace(
                type = ActionType.CREATE,
                data = challenge.idValue,
                dataType = Challenge::class.simpleName!!
            )
        ), null)
        if(createChallengeTx.result != SOAResultType.SUCCESS)
            return SOAResult(SOAResultType.FAILURE, createChallengeTx.message)

        // create a transaction for tokens received?
        GenerateTransactionService.execute(caller, TransactionNamespace(
            from = challenge.cryptoKeyPair.publicKey,
            to = userAccount.cryptoKeyPair.publicKey,
            previousTransaction = null,
            metadatas = MetadatasListNamespace(
                ChallengeMetadata(
                    challenge.idValue,
                    challenge.challengeSettings.offChain,
                    challenge.challengeSettings.maxShares
                ).getChallengeMetadataNamespaces()
            ),
            action = ActionNamespace(
                type = ActionType.SHARE,
                data = challenge.idValue,
                dataType = Challenge::class.simpleName!!
            )
        ), null)

        return SOAResult(SOAResultType.SUCCESS, null, challenge)
    }

    private fun createSubChallengesList(subChallengeIds: List<Pair<Int, SubChallengeType>>) : SizedCollection<SubChallenge> {
        var subChallenges = mutableListOf<SubChallenge>()
        subChallengeIds.forEach {
            subChallenges.add(SubChallenge.new {
                subChallenge = EntityID(it.first, Challenges)
                type = it.second
            })
        }
        return SizedCollection(subChallenges)
    }

    private fun createCompletionCriteria(completionCriteriaNamespace: CompletionCriteriaNamespace) : CompletionCriteria {
        // TODO think about how we handle generating completion criteria
        // TODO should this happen AFTER a challenge is created?
        // TODO how and when will the pool be populated

        return GenerateCompletionCriteriaService.execute(null, completionCriteriaNamespace, null).data!!
    }
}