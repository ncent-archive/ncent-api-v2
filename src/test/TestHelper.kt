package test

import framework.models.idValue
import framework.services.DaoService
import main.daos.*
import main.services.challenge.GenerateChallengeService
import main.services.completion_criteria.GenerateCompletionCriteriaService
import main.services.reward.AddToRewardPoolService
import main.services.reward.GenerateRewardService
import main.services.token.GenerateTokenService
import main.services.transaction.GenerateTransactionService
import main.services.user_account.GenerateUserAccountService
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.joda.time.DateTimeZone

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
        userAccount: UserAccount? = null,
        audience: Audience = Audience.PROVIDENCE,
        type: RewardTypeName = RewardTypeName.EVEN
    ): Reward {
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
                name = "nCent" + DateTime.now(DateTimeZone.UTC).millis,
                parentToken = null,
                parentTokenConversionRate = null
            )
        )

        var completionCriteriaNamespace = CompletionCriteriaNamespace(
            address = null,
            rewardNamespace = rewardNamespace,
            preReqChallengeIds = listOf()
        )

        // Create a user, token, reward, and add to the pool
        // Create a fake providence chain
        return transaction {
            var newUserAccount = if(userAccount == null) {
                GenerateUserAccountService.execute(
                    null,
                    mapOf(
                        Pair("firstname", "Arya"),
                        Pair("lastname", "Soltanieh"),
                        Pair("email", "as" + DateTime.now(DateTimeZone.UTC).millis + "@ncent.io")
                    )
                ).data!!
            } else {
                userAccount
            }

            val token = GenerateTokenService.execute(newUserAccount.idValue, nCentTokenNamespace, null).data!!
            var reward = GenerateRewardService.execute(newUserAccount.idValue, rewardNamespace, null).data!!
            val rewardPoolTx = AddToRewardPoolService.execute(
                newUserAccount.idValue,
                mapOf(
                    Pair("reward_id", reward.idValue.toString()),
                    Pair("name", token.tokenType.name),
                    Pair("amount", "10")
                )
            ).data!!

            var completionCriteria = GenerateCompletionCriteriaService.execute(newUserAccount.idValue, completionCriteriaNamespace, null).data!!
            completionCriteria.reward = reward

            return@transaction reward
        }
    }

    fun generateUserAccounts(count: Int = 1): List<UserAccount> {
        var userAccounts = mutableListOf<UserAccount>()
        for(i in 0..(count - 1)) {
            transaction {
                userAccounts.add(GenerateUserAccountService.execute(null, mutableMapOf(
                    Pair("email", "dev$i@ncnt.io"),
                    Pair("firstname", "dev$i"),
                    Pair("lastname", "ncnt$i")
                )).data!!)
            }
        }
        return userAccounts
    }

    fun generateFullChallenge(userAccount: UserAccount, subChallengeUserAccount: UserAccount, count: Int = 1, withReward: Boolean = false): List<Challenge> {
        return DaoService.execute {
            var challengesToReturn = mutableListOf<Challenge>()

            for(i in 0..(count - 1)) {
                val challenges = generateChallenge(subChallengeUserAccount, 3)
                val parentChallenge = challenges[0]
                var subChallengeList = mutableListOf<Pair<Challenge, SubChallengeType>>()
                subChallengeList.add(Pair(challenges[1], SubChallengeType.SYNC))
                subChallengeList.add(Pair(challenges[2], SubChallengeType.ASYNC))
                val distributionFeeRewardNamespace = TestHelper.generateRewardNamespace(RewardTypeName.SINGLE)
                val challengeSettingNamespace = TestHelper.generateChallengeSettingsNamespace(userAccount).first()
                val completionCriteriasNamespace = TestHelper.generateCompletionCriteriaNamespace(userAccount, 2)
                val completionCriteria1 = completionCriteriasNamespace[0]
                val challengeNamespace = ChallengeNamespace(
                    parentChallenge = parentChallenge.idValue,
                    challengeSettings = challengeSettingNamespace,
                    subChallenges = subChallengeList.map { Pair(it.first.idValue, it.second) },
                    completionCriteria = completionCriteria1,
                    distributionFeeReward = distributionFeeRewardNamespace
                )
                val challengeResult = GenerateChallengeService.execute(userAccount.idValue, challengeNamespace, null)
                val challenge = challengeResult.data!!
                if(withReward) {
                    challenge.completionCriterias = CompletionCriteria.find { CompletionCriterias.reward eq buildGenericReward(userAccount).id }.first()
                }
                challengesToReturn.add(challenge)
            }
            return@execute challengesToReturn
        }.data!!
    }

    fun generateChallenge(userAccount: UserAccount, count: Int = 1): List<Challenge> {
        var challengeNamespaces = generateChallengeNamespace(userAccount, count)
        return DaoService.execute {
            var challenges = mutableListOf<Challenge>()

            challengeNamespaces.forEach {
                val challengeResult = GenerateChallengeService.execute(userAccount.idValue, it, null)
                challenges.add(challengeResult.data!!)
            }
            return@execute challenges
        }.data!!
    }

    fun generateChallengeNamespace(userAccount: UserAccount, count: Int = 1): List<ChallengeNamespace> {
        var challengeSettingsList = generateChallengeSettingsNamespace(userAccount, count)
        var challengeDistributionReward = generateRewardNamespace(RewardTypeName.SINGLE)
        var challengeNamespaces = mutableListOf<ChallengeNamespace>()
        var completionCriteriaNamespace = generateCompletionCriteriaNamespace(userAccount)

        for(i in 0..(count - 1)) {
            val challengeNamespace = ChallengeNamespace(
                parentChallenge = null,
                challengeSettings = challengeSettingsList[i],
                distributionFeeReward = challengeDistributionReward,
                subChallenges = listOf(),
                completionCriteria = completionCriteriaNamespace.first()
            )
            challengeNamespaces.add(challengeNamespace)
        }
        return challengeNamespaces
    }

    fun generateChallengeSettingsNamespace(userAccount: UserAccount, count: Int = 1): List<ChallengeSettingNamespace> {
        var challengeSettingsList = mutableListOf<ChallengeSettingNamespace>()
        for(i in 0..(count - 1)) {
            challengeSettingsList.add(
                ChallengeSettingNamespace(
                    name = "TESTname$i",
                    description = "TESTdescription$i",
                    imageUrl = "TESTimageUrl$i",
                    sponsorName = "TESTsponsorName$i",
                    expiration = DateTime.now(DateTimeZone.UTC).plusDays(1),
                    admin = userAccount.idValue,
                    maxShares = 100,
                    offChain = false,
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

    fun generateCompletionCriteriaNamespace(userAccount: UserAccount, count: Int = 1): List<CompletionCriteriaNamespace> {
        var completionCriteriaNamespaces = mutableListOf<CompletionCriteriaNamespace>()
        for(i in 0..(count - 1)) {
            completionCriteriaNamespaces.add(
                CompletionCriteriaNamespace(
                    address = userAccount.cryptoKeyPair.publicKey,
                    rewardNamespace = generateRewardNamespace()
                )
            )
        }
        return completionCriteriaNamespaces
    }

    fun generateRewardNamespace(rewardTypeName: RewardTypeName = RewardTypeName.EVEN): RewardNamespace {
        return RewardNamespace(
            type = RewardTypeNamespace(
                audience = Audience.PROVIDENCE,
                type = rewardTypeName
            ), metadatas = null
        )
    }

    fun generateShareTransaction(challenge: Challenge, fromAccount: UserAccount, toAccount: UserAccount, previousTransaction: Transaction, amount: Int): Transaction {
        return GenerateTransactionService.execute(fromAccount.idValue, TransactionNamespace(
            from = fromAccount.cryptoKeyPair.publicKey,
            to = toAccount.cryptoKeyPair.publicKey,
            previousTransaction = previousTransaction.idValue,
            metadatas = MetadatasListNamespace(
                ChallengeMetadata(
                    challenge.idValue,
                    challenge.challengeSettings.offChain,
                    amount
                ).getChallengeMetadataNamespaces()
            ),
            action = ActionNamespace(
                type = ActionType.SHARE,
                data = challenge.idValue,
                dataType = Challenge::class.simpleName!!
            )
        ), null).data!!
    }
}