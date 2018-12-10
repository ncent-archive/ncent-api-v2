package main.services.challenge

import framework.services.DaoService
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.*
import main.services.completion_criteria.GenerateCompletionCriteriaService
import main.services.reward.GenerateRewardService
import main.services.user_account.GenerateCryptoKeyPairService
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.SizedCollection

/**
 * Create a challenge; generate all appropriate objects including transaction(s)
 */
object GenerateChallengeService: SOAServiceInterface<Challenge> {
    override fun execute(caller: Int?, d: Any?, params: Map<String, String>?) : SOAResult<Challenge> {
        val challengeNamespace = d!! as ChallengeNamespace
        return DaoService.execute {
            val settings = ChallengeSetting.new {
                name = challengeNamespace.challengeSettings.name
                description = challengeNamespace.challengeSettings.description
                imageUrl = challengeNamespace.challengeSettings.imageUrl
                sponsorName = challengeNamespace.challengeSettings.sponsorName
                expiration = challengeNamespace.challengeSettings.expiration
                admin = EntityID(challengeNamespace.challengeSettings.admin, UserAccounts)
                offChain = challengeNamespace.challengeSettings.offChain
                maxRewards = challengeNamespace.challengeSettings.maxRewards
                maxDistributionFeeReward = challengeNamespace.challengeSettings.maxDistributionFeeReward
                maxSharesPerReceivedShare = challengeNamespace.challengeSettings.maxSharesPerReceivedShare
                maxDepth = challengeNamespace.challengeSettings.maxDepth
                maxNodes = challengeNamespace.challengeSettings.maxNodes
            }

            val keyPairGenerated = GenerateCryptoKeyPairService.execute()
            if(keyPairGenerated.result != SOAResultType.SUCCESS)
                throw Exception(keyPairGenerated.message)

            val optionalParentChallenge = if(challengeNamespace.parentChallenge != null)
                Challenge.findById(challengeNamespace.parentChallenge)
            else
                null

            val distributionFeeRewardResult = GenerateRewardService.execute(null, challengeNamespace.distributionFeeReward, null)

            // TODO add reward to pool?
            val challenge = Challenge.new {
                parentChallenge = optionalParentChallenge
                challengeSettings = settings
                cryptoKeyPair = keyPairGenerated.data!!
                distributionFeeReward = distributionFeeRewardResult.data!!
            }

            challenge.completionCriterias = createCompletionCriteriasList(challengeNamespace.completionCriterias)

            if(challengeNamespace.asyncSubChallenges.any()) {
                challenge.asyncSubChallenges = createSubChallengesList(challengeNamespace.asyncSubChallenges, SubChallengeType.ASYNC)
            }

            if(challengeNamespace.syncSubChallenges.any()) {
                challenge.asyncSubChallenges = createSubChallengesList(challengeNamespace.syncSubChallenges, SubChallengeType.SYNC)
            }
            // TODO create a transaction for challenge creation state
            // TODO create a transaction for tokens received?

            return@execute challenge
        }
    }

    private fun createSubChallengesList(subChallengeIds: List<Int>, subChallengeType: SubChallengeType) : SizedCollection<SubChallenge> {
        var subChallenges = mutableListOf<SubChallenge>()
        subChallengeIds.forEach {
            subChallenges.add(SubChallenge.new {
                subChallenge = EntityID(it, Challenges)
                type = subChallengeType
            })
        }
        return SizedCollection(subChallenges)
    }

    private fun createCompletionCriteriasList(completionCriteriaNamespaces: List<CompletionCriteriaNamespace>) : SizedCollection<CompletionCriteria> {
        var completionCriterias = mutableListOf<CompletionCriteria>()

        // TODO think about how we handle generating completion criteria
        // TODO should this happen AFTER a challenge is created?
        // TODO how and when will the pool be populated
        completionCriteriaNamespaces.forEach {
            completionCriterias.add(
                GenerateCompletionCriteriaService.execute(null, it, null).data!!
            )
        }
        return SizedCollection(completionCriterias)
    }
}