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
    fun buildGenericProvidenceChain(): List<Transaction> {
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
            var tx1 = GenerateTransactionService.execute(transactionNamespace).data!!
            var transaction2Namespace = TransactionNamespace(from = "ARYA2", to = "MIKE2", action = action, previousTransaction = tx1.idValue, metadatas = metadatas)
            var tx2 = GenerateTransactionService.execute(transaction2Namespace).data!!
            var transaction3Namespace = TransactionNamespace(from = "ARYA3", to = "MIKE3", action = action, previousTransaction = tx2.idValue, metadatas = metadatas)
            var transaction4Namespace = TransactionNamespace(from = "ARYA4", to = "MIKE4", action = action, previousTransaction = tx2.idValue, metadatas = metadatas)
            var tx3 = GenerateTransactionService.execute(transaction3Namespace).data!!
            var tx4 = GenerateTransactionService.execute(transaction4Namespace).data!!
            var transaction5Namespace = TransactionNamespace(from = "ARYA5", to = "MIKE5", action = action, previousTransaction = tx4.idValue, metadatas = metadatas)
            var transaction6Namespace = TransactionNamespace(from = "ARYA6", to = "MIKE6", action = action, previousTransaction = tx4.idValue, metadatas = metadatas)
            var tx5 = GenerateTransactionService.execute(transaction5Namespace).data!!
            var tx6 = GenerateTransactionService.execute(transaction6Namespace).data!!
            return@transaction listOf(tx1, tx2, tx3, tx4, tx5, tx6)
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
                        "as" + DateTime.now(DateTimeZone.UTC).millis + "@ncent.io",
                        "Arya",
                        "Soltanieh"
                ).data!!.value
            } else {
                userAccount
            }

            val token = GenerateTokenService.execute(newUserAccount, nCentTokenNamespace).data!!
            var reward = GenerateRewardService.execute(rewardNamespace).data!!
            val rewardPoolTx = AddToRewardPoolService.execute(
                newUserAccount,
                reward.idValue,
                token.tokenType.name,
                10.0
            ).data!!

            var completionCriteria = GenerateCompletionCriteriaService.execute(newUserAccount, completionCriteriaNamespace).data!!
            completionCriteria.reward = reward

            return@transaction reward
        }
    }

    fun generateUserAccounts(count: Int = 1): List<NewUserAccount> {
        var newUserAccounts = mutableListOf<NewUserAccount>()
        for(i in 0..(count - 1)) {
            transaction {
                newUserAccounts.add(GenerateUserAccountService.execute(
                        "dev$i@ncnt.io",
                        "dev$i",
                        "ncnt$i"
                ).data!!)
            }
        }
        return newUserAccounts
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
                val challengeResult = GenerateChallengeService.execute(userAccount, challengeNamespace)
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
                val challengeResult = GenerateChallengeService.execute(userAccount, it)
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
        val exp = DateTime.now(DateTimeZone.UTC).plusDays(1).toString()
        for(i in 0..(count - 1)) {
            challengeSettingsList.add(
                ChallengeSettingNamespace(
                    name = "TESTname$i",
                    description = "TESTdescription$i",
                    imageUrl = "TESTimageUrl$i",
                    sponsorName = "TESTsponsorName$i",
                    expiration = exp,
                    shareExpiration = exp,
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
        return GenerateTransactionService.execute(TransactionNamespace(
            from = fromAccount.cryptoKeyPair.publicKey,
            to = toAccount.cryptoKeyPair.publicKey,
            previousTransaction = previousTransaction.idValue,
            metadatas = MetadatasListNamespace(
                ChallengeMetadata(
                    challenge.idValue,
                    challenge.challengeSettings.offChain,
                    challenge.challengeSettings.shareExpiration.toString(),
                    amount
                ).getChallengeMetadataNamespaces()
            ),
            action = ActionNamespace(
                type = ActionType.SHARE,
                data = challenge.idValue,
                dataType = Challenge::class.simpleName!!
            )
        )).data!!
    }
}