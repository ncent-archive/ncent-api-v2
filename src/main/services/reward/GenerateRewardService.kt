package main.services.reward

import framework.services.DaoService
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.*
import main.services.user_account.GenerateCryptoKeyPairService
import org.jetbrains.exposed.sql.SizedCollection
import java.lang.Exception

/**
 * Generate a reward if it is valid
 */
object GenerateRewardService: SOAServiceInterface<Reward> {
    override fun execute(caller: Int?, d: Any?, params: Map<String, String>?) : SOAResult<Reward> {
        val tokenNamespace = d!! as RewardNamespace
        return DaoService.execute {
            // TODO
            // find or create a reward type
            val rewardTypes = RewardType.find {
                RewardTypes.audience eq tokenNamespace.type.audience
                RewardTypes.type eq tokenNamespace.type.type
            }

            val rewardType = if(rewardTypes.empty()) {
                RewardType.new {
                    audience = tokenNamespace.type.audience
                    type = tokenNamespace.type.type
                }
            } else {
                rewardTypes.first()
            }

            val metadatasToAdd = if(tokenNamespace.metadatas != null) {
                tokenNamespace.metadatas.metadatas.map {
                        md -> Metadata.new {
                        key = md.key
                        value = md.value
                    }
                }
            } else {
                listOf()
            }

            val keyPairResult = GenerateCryptoKeyPairService.execute()
            if(keyPairResult.result != SOAResultType.SUCCESS)
                return@execute throw Exception(keyPairResult.message)
            val keyPairNamespace: CryptoKeyPairNamespace = keyPairResult.data!!

            val keyPair = CryptoKeyPair.new {
                publicKey = keyPairNamespace.publicKey
                encryptedPrivateKey = keyPairNamespace.encryptedPrivateKey
            }

            val newReward = Reward.new {
                type = rewardType
            }

            val rewardPool = RewardPool.new {
                cryptoKeyPair = keyPair
            }

            newReward.pool = rewardPool
            newReward.metadatas = SizedCollection(metadatasToAdd)

            return@execute newReward
        }
    }
}