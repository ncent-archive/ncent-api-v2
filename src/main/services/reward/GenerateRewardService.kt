package main.services.reward

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.*
import main.services.user_account.GenerateCryptoKeyPairService
import org.jetbrains.exposed.sql.SizedCollection

/**
 * Generate a reward if it is valid
 */
object GenerateRewardService: SOAServiceInterface<Reward> {
    override fun execute(caller: Int?, d: Any?, params: Map<String, String>?) : SOAResult<Reward> {
        val rewardNamespace = d!! as RewardNamespace
        // find or create a reward type
        val rewardTypes = RewardType.find {
            RewardTypes.audience eq rewardNamespace.type.audience
            RewardTypes.type eq rewardNamespace.type.type
        }

        val rewardType = if(rewardTypes.empty()) {
            RewardType.new {
                audience = rewardNamespace.type.audience
                type = rewardNamespace.type.type
            }
        } else {
            rewardTypes.first()
        }

        val keyPairResult = GenerateCryptoKeyPairService.execute()
        if(keyPairResult.result != SOAResultType.SUCCESS)
            return SOAResult(SOAResultType.FAILURE, keyPairResult.message)

        val keyPair = CryptoKeyPair.new {
            publicKey = keyPairResult.data!!.publicKey
            privateKey = keyPairResult.data!!.privateKey
        }

        val newReward = Reward.new {
            type = rewardType
        }

        val rewardPool = RewardPool.new {
            cryptoKeyPair = keyPair
        }

        newReward.pool = rewardPool

        if(rewardNamespace.metadatas != null) {
            newReward.metadatas = SizedCollection(rewardNamespace.metadatas.metadatas.map {
                    md -> Metadata.new {
                    key = md.key
                    value = md.value
                }
            })
        }

        return SOAResult(SOAResultType.SUCCESS, null, newReward)
    }
}