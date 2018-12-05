package main.services.challenge

import framework.services.DaoService
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.*
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

            val challenge = Challenge.new {
                parentChallenge = optionalParentChallenge
                challengeSettings = settings
                cryptoKeyPair = keyPairGenerated.data!!
            }

            if(challengeNamespace.asyncSubChallenges.any()) {
                challenge.asyncSubChallenges = createSubChallengesList(challenge.id, challengeNamespace.asyncSubChallenges)
            }

            if(challengeNamespace.syncSubChallenges.any()) {
                challenge.asyncSubChallenges = createSubChallengesList(challenge.id, challengeNamespace.syncSubChallenges)
            }

            return@execute challenge
        }
    }

    private fun createSubChallengesList(challengeId: EntityID<Int>, subChallengeIdsAndTypes: List<Pair<Int, SubChallengeType>>) : SizedCollection<SubChallenge> {
        var subChallenges = mutableListOf<SubChallenge>()
        subChallengeIdsAndTypes.forEach {
            subChallenges.add(SubChallenge.new {
                parentChallenge = challengeId
                subChallenge = EntityID(it.first, Challenges)
                type = it.second
            })
        }
        return SizedCollection(subChallenges)
    }
}