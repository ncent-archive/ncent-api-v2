package main.daos

import com.tinder.StateMachine
import framework.models.BaseIntEntity
import framework.models.BaseIntEntityClass
import framework.models.BaseIntIdTable
import framework.models.idValue
import framework.services.DaoService
import kotlinserverless.framework.models.Handler
import kotlinserverless.framework.services.SOAResultType
import main.services.transaction.GenerateTransactionService
import main.services.transaction.GetTransactionsService
import org.apache.log4j.Priority
import org.jetbrains.exposed.dao.*

/**
 * Used to represent challenges and store pointers to models that
 * store the state for a challenge over the lifecycle of that challenge
 *
 * @property id
 * @property parentChallenge the parent of this challenge, typically this challenge must be completed before the parent
 * @property challengeSettings ChallengeSettings
 * @property asyncSubChallenges sub challenges that can be completed in any order
 * @property syncSubChallenges sub challenges that must be completed in order
 * @property completionCriterias CompletionCriterias
 * @property distributionFeeReward the distribution fees and the pool. this is the
 * pool that will be drawn on if anybody 'opts-out' of attempting to help.
 */
class Challenge(id: EntityID<Int>) : BaseIntEntity(id, Challenges) {
    companion object : BaseIntEntityClass<Challenge>(Challenges)

    var parentChallenge by Challenge optionalReferencedOn Challenges.parentChallenge
    var challengeSettings by ChallengeSetting referencedOn Challenges.challengeSettings
    var asyncSubChallenges by SubChallenge via ChallengeToSubChallenges
    var syncSubChallenges by SubChallenge via ChallengeToSubChallenges
    var completionCriterias by CompletionCriteria via ChallengeToCompletionCriterias
    var cryptoKeyPair by CryptoKeyPair referencedOn Challenges.cryptoKeyPair
    var distributionFeeReward by Reward referencedOn Challenges.distributionFeeReward

    val stateMachine = StateMachine.create<ChallengeState, ChallengeEvent, ChallengeSideEffect> {
        initialState(ChallengeState.CREATED)
        state<ChallengeState.CREATED> {
            onEnter {
                ChallengeSideEffect.LogCreated
            }
            on<ChallengeEvent.OnActivated> {
                transitionTo(ChallengeState.ACTIVE, ChallengeSideEffect.LogActivated)
            }
            on<ChallengeEvent.OnInvalidated> {
                transitionTo(ChallengeState.INVALID, ChallengeSideEffect.LogInvalidated)
            }
            on<ChallengeEvent.OnExpired> {
                transitionTo(ChallengeState.EXPIRED, ChallengeSideEffect.LogExpired)
            }

        }
        state<ChallengeState.ACTIVE> {
            on<ChallengeEvent.OnCompleted> {
                transitionTo(ChallengeState.COMPLETE, ChallengeSideEffect.LogCompleted)
            }
            on<ChallengeEvent.OnInvalidated> {
                transitionTo(ChallengeState.INVALID, ChallengeSideEffect.LogInvalidated)
            }
            on<ChallengeEvent.OnExpired> {
                transitionTo(ChallengeState.EXPIRED, ChallengeSideEffect.LogExpired)
            }

        }
        state<ChallengeState.INVALID> {
            on<ChallengeEvent.OnActivated> {
                transitionTo(ChallengeState.ACTIVE, ChallengeSideEffect.LogActivated)
            }
            on<ChallengeEvent.OnExpired> {
                transitionTo(ChallengeState.EXPIRED, ChallengeSideEffect.LogExpired)
            }
        }
        onTransition {
            val validTransition = it as? StateMachine.Transition.Valid ?: return@onTransition
            when (validTransition.sideEffect) {
                ChallengeSideEffect.LogCreated -> logAndGenerateTransaction(it, ActionType.CREATE)
                ChallengeSideEffect.LogActivated -> logAndGenerateTransaction(it, ActionType.ACTIVATE)
                ChallengeSideEffect.LogCompleted -> logAndGenerateTransaction(it, ActionType.COMPLETE)
                ChallengeSideEffect.LogInvalidated -> logAndGenerateTransaction(it, ActionType.INVALIDATE)
                ChallengeSideEffect.LogExpired -> logAndGenerateTransaction(it, ActionType.EXPIRE)
            }
        }
    }

    val stateChangeActionTypes = setOf(ActionType.CREATE, ActionType.ACTIVATE, ActionType.COMPLETE, ActionType.INVALIDATE, ActionType.EXPIRE)

    private fun getTransactions(): List<Transaction>? {
        return GetTransactionsService.execute(
            null,
            mapOf(
                Pair("dataType", "Challenge"),
                Pair("data", idValue.toString())
            )
        ).data?.transactions
    }

    fun getLastStateChangeTransaction(): Transaction? {
        getTransactions()?.forEach {
            if(stateChangeActionTypes.contains(it.action.type))
                return it
        }
        return null
    }

    private fun logAndGenerateTransaction(stateTransition: StateMachine.Transition<ChallengeState, ChallengeEvent, ChallengeSideEffect>, state: ActionType): Transaction {
        val previousTransactionId = getLastStateChangeTransaction()?.idValue
        val transactionResult = DaoService.execute {
            val transactionNamespace = TransactionNamespace(
                from = cryptoKeyPair.publicKey,
                to = null,
                action = ActionNamespace(
                    type = state,
                    data = idValue,
                    dataType = "Challenge"
                ),
                previousTransaction = previousTransactionId,
                metadatas = null
            )
            val transactionResult = GenerateTransactionService.execute(null, transactionNamespace, null)
            if(transactionResult != SOAResultType.SUCCESS)
                throw Exception(transactionResult.message)
            Handler.log().log(
                    Priority.INFO,
                    "Transitioning state for challenge {" +
                                "id: ${idValue}, " +
                                "fromState: ${stateTransition.fromState}, " +
                                "toState: ${state}, " +
                                "previousTransactionId: ${previousTransactionId}, " +
                                "transactionId: ${transactionResult.data!!.idValue} " +
                            "}"
            )
            return@execute transactionResult.data!!
        }
        if(transactionResult != SOAResultType.SUCCESS)
            throw Exception(transactionResult.message)
        return transactionResult.data!!
    }
}

class ChallengeList(val challenges: List<Challenge>)

object Challenges : BaseIntIdTable("challenges") {
    val parentChallenge = reference("parent_challenge", Challenges).nullable()
    val challengeSettings = reference("challenge_settings", ChallengeSettings)
    val cryptoKeyPair = reference("crypto_key_pair", CryptoKeyPairs)
    val distributionFeeReward = reference("distribution_fee_reward", Rewards)
}

class SubChallenge(id: EntityID<Int>) : BaseIntEntity(id, SubChallenges) {
    companion object : BaseIntEntityClass<SubChallenge>(SubChallenges)

    var subChallenge by SubChallenges.subChallenge
    var type by SubChallenges.type
}

object ChallengeToCompletionCriterias : BaseIntIdTable("challenge_to_completion_criteria") {
    val challenge = reference("challenge_to_completion_criteria", Challenges).primaryKey()
    val completionCriteria = reference("completion_criteria_to_challenge", CompletionCriterias).primaryKey()
}

object ChallengeToSubChallenges : BaseIntIdTable("challenge_to_sub_challenges") {
    val challenge = reference("challenge_to_sub_challenge", Challenges).primaryKey()
    val subChallenge = reference("sub_challenge_to_challenge", SubChallenges).primaryKey()
}

object SubChallenges : BaseIntIdTable("sub_challenge") {
    val subChallenge = reference("sub_challenge_to_challenge", Challenges).primaryKey()
    val type = enumeration("sub_challenge_type", SubChallengeType::class)
}

data class ChallengeNamespace(
    val parentChallenge: Int?,
    val challengeSettings: ChallengeSettingNamespace,
    val asyncSubChallenges: List<Int>,
    val syncSubChallenges: List<Int>,
    val completionCriterias: List<CompletionCriteriaNamespace>,
    val distributionFeeReward: RewardNamespace
)

enum class SubChallengeType {
    SYNC, ASYNC
}

/**
 * This data type will be used to generate a map for transaction metadata
 * when generating a challenge based transaction
 * This will store information about that particular transaction ex:
 * when receiving a shared challenge the transaction data will hold how many shares you have available to make
 */
data class ChallengeMetadata(
    val challengeId: Int,
    val offChain: Boolean,
    val maxShares: Int?
) {
    fun getChallengeMetadataNamespaces(): List<MetadatasNamespace> {
        var challengeMetadatas = mutableListOf<MetadatasNamespace>()
        challengeMetadatas.add(MetadatasNamespace("challengeId", challengeId.toString()))
        challengeMetadatas.add(MetadatasNamespace("offChain", offChain.toString()))
        if(maxShares != null)
            challengeMetadatas.add(MetadatasNamespace("maxShares", maxShares.toString()))
        return challengeMetadatas
    }
}

/**
 * CREATED -- initial state for a challenge
 * ACTIVE -- on starting a challenge, this is the 'initial' started state
 * COMPLETE -- [final]
 * INVALID -- if there is something wrong with this challenge (ex: funds depleted)
 * EXPIRED -- [final]
 */
sealed class ChallengeState {
    object CREATED : ChallengeState()
    object ACTIVE : ChallengeState()
    object COMPLETE : ChallengeState()
    object INVALID : ChallengeState()
    object EXPIRED : ChallengeState()
}

sealed class ChallengeEvent {
    object OnCreated : ChallengeEvent()
    object OnActivated : ChallengeEvent()
    object OnCompleted : ChallengeEvent()
    object OnInvalidated : ChallengeEvent()
    object OnExpired : ChallengeEvent()
}

sealed class ChallengeSideEffect {
    object LogCreated : ChallengeSideEffect()
    object LogActivated : ChallengeSideEffect()
    object LogCompleted : ChallengeSideEffect()
    object LogInvalidated : ChallengeSideEffect()
    object LogExpired : ChallengeSideEffect()
}