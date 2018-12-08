package test

import framework.models.idValue
import framework.services.DaoService
import io.kotlintest.days
import io.kotlintest.forExactly
import io.kotlintest.properties.forAll
import main.daos.*
import main.services.challenge.GenerateChallengeSettingsService
import main.services.completion_criteria.GenerateCompletionCriteriaService
import main.services.reward.AddToRewardPoolService
import main.services.reward.GenerateRewardService
import main.services.token.GenerateTokenService
import main.services.transaction.GenerateTransactionService
import main.services.user_account.GenerateCryptoKeyPairService
import main.services.user_account.GenerateUserAccountService
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.dateTimeParam
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import java.sql.Time

object TestHelper {

    /**
     * Building a tree:
     *         ARYA
     *           |
     *         ARYA2
     *         /    \
     *      ARYA3   ARYA4
     *             /    \
     *          ARYA5   ARYA6
     *
     * Returns [endTransactionId: ARYA6 ,sideTransactionId: ARYA4]
     */
    fun buildGenericProvidenceChain(): List<EntityID<Int>> {
        var action = ActionNamespace(
            type = ActionType.CREATE,
            data = 1,
            dataType = "UserAccount"
        )
        var metadatas = MetadatasListNamespace(
            listOf(
                MetadatasNamespace("city", "san carlos"),
                MetadatasNamespace("state", "california")
            )
        )

        return transaction {
            var transactionNamespace = TransactionNamespace(from = "ARYA", to = "MIKE", action = action, previousTransaction = null, metadatas = metadatas)
            var tx1 = GenerateTransactionService.execute(null, transactionNamespace, null).data!!
            var transaction2Namespace = TransactionNamespace(from = "ARYA2", to = "MIKE2", action = action, previousTransaction = tx1.idValue, metadatas = metadatas)
            var tx2 = GenerateTransactionService.execute(null, transaction2Namespace, null).data!!
            var transaction3Namespace = TransactionNamespace(from = "ARYA3", to = "MIKE3", action = action, previousTransaction = tx2.idValue, metadatas = metadatas)
            var transaction4Namespace = TransactionNamespace(from = "ARYA4", to = "MIKE4", action = action, previousTransaction = tx2.idValue, metadatas = metadatas)
            var tx3 = GenerateTransactionService.execute(null, transaction3Namespace, null).data!!
            var tx4 = GenerateTransactionService.execute(null, transaction4Namespace, null).data!!
            var transaction5Namespace = TransactionNamespace(from = "ARYA5", to = "MIKE5", action = action, previousTransaction = tx4.idValue, metadatas = metadatas)
            var transaction6Namespace = TransactionNamespace(from = "ARYA6", to = "MIKE6", action = action, previousTransaction = tx4.idValue, metadatas = metadatas)
            var tx5 = GenerateTransactionService.execute(null, transaction5Namespace, null).data!!
            var tx6 = GenerateTransactionService.execute(null, transaction6Namespace, null).data!!
            return@transaction listOf(tx1.id, tx2.id, tx3.id, tx4.id, tx5.id, tx6.id)
        }
    }

    fun buildGenericReward(
        audience: Audience = Audience.PROVIDENCE,
        type: RewardTypeName = RewardTypeName.EVEN
    ): EntityID<Int> {
        var rewardNamespace = RewardNamespace(
            type = RewardTypeNamespace(
                audience = audience,
                type = type
            ),
            metadatas = MetadatasListNamespace(
                listOf(MetadatasNamespace("title", "reward everyone"))
            )
        )
        var nCentTokenNamespace = TokenNamespace(
            amount = 100,
            tokenType = TokenTypeNamespace(
                id = null,
                name = "nCent" + DateTime.now().millis,
                parentToken = null,
                parentTokenConversionRate = null
            )
        )

        var completionCriteriaNamespace = CompletionCriteriaNamespace(
            address = null,
            rewardNamespace = rewardNamespace,
            expiration = DateTime.now().plusDays(100),
            preReqCompletionCriteriaIds = listOf()
        )

        // Create a user, token, reward, and add to the pool
        // Create a fake providence chain
        return transaction {
            var newUserAccount = GenerateUserAccountService.execute(
                null,
                mapOf(
                    Pair("firstname", "Arya"),
                    Pair("lastname", "Soltanieh"),
                    Pair("email", "as" + DateTime.now().millis + "@ncent.io")
                )
            ).data!!

            val token = GenerateTokenService.execute(newUserAccount.idValue, nCentTokenNamespace, null).data!!
            var rewards = GenerateRewardService.execute(newUserAccount.idValue, rewardNamespace, null).data!!
            val rewardPoolTx = AddToRewardPoolService.execute(
                newUserAccount.idValue,
                mapOf(
                    Pair("reward_id", rewards.idValue.toString()),
                    Pair("name", token.tokenType.name),
                    Pair("amount", "10")
                )
            ).data!!

            GenerateCompletionCriteriaService.execute(newUserAccount.idValue, completionCriteriaNamespace, null).data!!
            return@transaction rewards.id
        }
    }

    fun generateUserAccounts(count: Int = 1): List<UserAccount> {
        var userAccounts = mutableListOf<UserAccount>()
        for(i in 0..(count - 1)) {
            userAccounts.add(GenerateUserAccountService.execute(null, mutableMapOf(
                Pair("email", "dev$i@ncnt.io"),
                Pair("firstname", "dev$i"),
                Pair("lastname", "ncnt$i")
            )).data!!)
        }
        return userAccounts
    }

    fun generateChallenge(userAccount: UserAccount, count: Int = 1): List<Challenge> {
        var challengeSettingsList = generateChallengeSettingsNamespace(userAccount, count)
        return DaoService.execute {
            var challenges = mutableListOf<Challenge>()
            for(i in 0..count) {
                challenges.add(Challenge.new {
                    challengeSettings = GenerateChallengeSettingsService.execute(null, challengeSettingsList[i], null).data!!
                    cryptoKeyPair = GenerateCryptoKeyPairService.execute().data!!
                })
            }
            return@execute challenges
        }.data!!
    }

    fun generateChallengeSettingsNamespace(userAccount: UserAccount, count: Int = 1): List<ChallengeSettingNamespace> {
        var challengeSettingsList = mutableListOf<ChallengeSettingNamespace>()
        for(i in 0..count) {
            challengeSettingsList.add(
                ChallengeSettingNamespace(
                    name = "TESTname$i",
                    description = "TESTdescription$i",
                    imageUrl = "TESTimageUrl$i",
                    sponsorName = "TESTsponsorName$i",
                    expiration = DateTime.now().plusDays(1),
                    admin = userAccount.idValue,
                    offChain = null,
                    maxRewards = null,
                    maxDistributionFeeReward = null,
                    maxSharesPerReceivedShare = null,
                    maxDepth = null,
                    maxNodes = null
                )
            )
        }
        return challengeSettingsList.toList()
    }

    fun generateResultVectorNamespace(userAccount: UserAccount, count: Int = 1): List<ResultVectorNamespace> {

    }
}