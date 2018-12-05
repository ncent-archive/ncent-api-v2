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

            val challenge = Challenge.new {
                challengeSettings = settings
                cryptoKeyPair = keyPairGenerated.data!!
            }

            if(challengeNamespace.asyncSubChallenges.any()) {
                var asyncSubChallengesList = mutableListOf<SubChallenge>()
                challengeNamespace.asyncSubChallenges.forEach {
                    asyncSubChallengesList.add(SubChallenge.new {
                        parentChallenge = challenge.id
                        subChallenge = EntityID(it.first, Challenges)
                        type = it.second
                    })
                }

                challenge.asyncSubChallenges = SizedCollection(asyncSubChallengesList)
            }

            if(challengeNamespace.syncSubChallenges.any()) {
                var syncSubChallengesList = mutableListOf<SubChallenge>()
                challengeNamespace.syncSubChallenges.forEach {
                    syncSubChallengesList.add(SubChallenge.new {
                        parentChallenge = challenge.id
                        subChallenge = EntityID(it.first, Challenges)
                        type = it.second
                    })
                }

                challenge.asyncSubChallenges = SizedCollection(syncSubChallengesList)
            }

            return@execute challenge
        }
    }
}