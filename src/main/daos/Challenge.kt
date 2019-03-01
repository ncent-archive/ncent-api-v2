package main.daos

import framework.models.*
import main.services.transaction.GetTransactionsService
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.sql.ReferenceOption
import org.joda.time.DateTime
import org.joda.time.DateTimeZone

/**
 * Used to represent challenges and store pointers to models that
 * store the state for a challenge over the lifecycle of that challenge
 *
 * @property id
 * @property parentChallenge the parent of this challenge, typically this challenge must be completed before the parent
 * @property challengeSettings ChallengeSettings
 * @property subChallenges sub challenges
 * @property completionCriterias CompletionCriterias
 * @property distributionFeeReward the distribution fees and the pool. this is the
 * pool that will be drawn on if anybody 'opts-out' of attempting to help.
 */
class Challenge(id: EntityID<Int>) : BaseIntEntity(id, Challenges) {
    companion object : BaseIntEntityClass<Challenge>(Challenges)

    var parentChallenge by Challenge optionalReferencedOn Challenges.parentChallenge
    var challengeSettings by ChallengeSetting referencedOn Challenges.challengeSettings
    var subChallenges by SubChallenge via ChallengeToSubChallenges
    var completionCriterias by CompletionCriteria referencedOn Challenges.completionCriteria
    var cryptoKeyPair by CryptoKeyPair referencedOn Challenges.cryptoKeyPair
    var distributionFeeReward by Reward referencedOn Challenges.distributionFeeReward

    override fun toMap(): MutableMap<String, Any?> {
        val map = super.toMap()
        map["parentChallenge"] = parentChallenge?.idValue?.toString()
        map["challengeSettings"] = challengeSettings.toMap()
        map["subChallenges"] = subChallenges.map { it.toMap() }
        map["completionCriteria"] = completionCriterias.toMap()
        map["cryptoKeyPair"] = cryptoKeyPair.toMap()
        map["distributionFeeReward"] = distributionFeeReward.toMap()
        return map
    }

    fun canTransitionState(fromState: ActionType, toState: ActionType): Boolean {
        val result =  when(fromState) {
            ActionType.COMPLETE -> {
                false
            }
            ActionType.CREATE -> {
                return arrayListOf(
                    ActionType.ACTIVATE,
                    ActionType.EXPIRE,
                    ActionType.INVALIDATE
                ).contains(toState)
            }
            ActionType.EXPIRE -> {
                false
            }
            ActionType.INVALIDATE -> {
                return arrayListOf(
                    ActionType.ACTIVATE,
                    ActionType.EXPIRE
                ).contains(toState)
            }
            ActionType.ACTIVATE -> {
                return arrayListOf(
                    ActionType.COMPLETE,
                    ActionType.EXPIRE,
                    ActionType.INVALIDATE
                ).contains(toState)
            }
            else -> throw Exception("Could not find challenge state to move to for ${fromState.type}")
        }
        return if(result && toState == ActionType.EXPIRE) {
            result && shouldExpire()
        } else {
            result
        }
    }

    fun shouldExpire(): Boolean {
        return challengeSettings.expiration < DateTime.now(DateTimeZone.UTC)
    }

// TODO Look into how we can use the state machine
//    val stateMachine = StateMachine.create<ChallengeState, ChallengeEvent, ChallengeSideEffect> {
//        initialState(ChallengeState.CREATED)
//        state<ChallengeState.CREATED> {
//            onEnter {
//                ChallengeSideEffect.LogCreated
//            }
//            on<ChallengeEvent.OnActivated> {
//                transitionTo(ChallengeState.ACTIVE, ChallengeSideEffect.LogActivated)
//            }
//            on<ChallengeEvent.OnInvalidated> {
//                transitionTo(ChallengeState.INVALID, ChallengeSideEffect.LogInvalidated)
//            }
//            on<ChallengeEvent.OnExpired> {
//                transitionTo(ChallengeState.EXPIRED, ChallengeSideEffect.LogExpired)
//            }
//
//        }
//        state<ChallengeState.ACTIVE> {
//            on<ChallengeEvent.OnCompleted> {
//                transitionTo(ChallengeState.COMPLETE, ChallengeSideEffect.LogCompleted)
//            }
//            on<ChallengeEvent.OnInvalidated> {
//                transitionTo(ChallengeState.INVALID, ChallengeSideEffect.LogInvalidated)
//            }
//            on<ChallengeEvent.OnExpired> {
//                transitionTo(ChallengeState.EXPIRED, ChallengeSideEffect.LogExpired)
//            }
//
//        }
//        state<ChallengeState.INVALID> {
//            on<ChallengeEvent.OnActivated> {
//                transitionTo(ChallengeState.ACTIVE, ChallengeSideEffect.LogActivated)
//            }
//            on<ChallengeEvent.OnExpired> {
//                transitionTo(ChallengeState.EXPIRED, ChallengeSideEffect.LogExpired)
//            }
//        }
//        onTransition {
//            val validTransition = it as? StateMachine.Transition.Valid ?: return@onTransition
//            when (validTransition.sideEffect) {
//                ChallengeSideEffect.LogCreated -> logAndGenerateTransaction(it, ActionType.CREATE)
//                ChallengeSideEffect.LogActivated -> logAndGenerateTransaction(it, ActionType.ACTIVATE)
//                ChallengeSideEffect.LogCompleted -> logAndGenerateTransaction(it, ActionType.COMPLETE)
//                ChallengeSideEffect.LogInvalidated -> logAndGenerateTransaction(it, ActionType.INVALIDATE)
//                ChallengeSideEffect.LogExpired -> logAndGenerateTransaction(it, ActionType.EXPIRE)
//            }
//        }
//    }

    val stateChangeActionTypes = setOf(ActionType.CREATE, ActionType.ACTIVATE, ActionType.COMPLETE, ActionType.INVALIDATE, ActionType.EXPIRE)

    private fun getTransactions(): List<Transaction>? {
        return GetTransactionsService.execute(
            null,
            cryptoKeyPair.publicKey,
            null,
            ActionNamespace(null, idValue, "Challenge")
        ).data?.transactions
    }

    fun getLastStateChangeTransaction(): Transaction? {
        getTransactions()?.forEach {
            if(stateChangeActionTypes.contains(it.action.type))
                return it
        }
        return null
    }

// TODO if we want to use state machine use this:
//    private fun logAndGenerateTransaction(stateTransition: StateMachine.Transition<ChallengeState, ChallengeEvent, ChallengeSideEffect>, state: ActionType): Transaction {
//        val previousTransactionId = getLastStateChangeTransaction()?.idValue
//        val transactionResult = DaoService.execute {
//            val transactionNamespace = TransactionNamespace(
//                from = cryptoKeyPair.publicKey,
//                to = null,
//                action = ActionNamespace(
//                    type = state,
//                    data = idValue,
//                    dataType = "Challenge"
//                ),
//                previousTransaction = previousTransactionId,
//                metadatas = null
//            )
//            val transactionResult = GenerateTransactionService.execute(null, transactionNamespace, null)
//            if(transactionResult != SOAResultType.SUCCESS)
//                throw Exception(transactionResult.message)
//            Handler.log().log(
//                    Priority.INFO,
//                    "Transitioning state for challenge {" +
//                                "id: ${idValue}, " +
//                                "fromState: ${stateTransition.fromState}, " +
//                                "toState: ${state}, " +
//                                "previousTransactionId: ${previousTransactionId}, " +
//                                "transactionId: ${transactionResult.data!!.idValue} " +
//                            "}"
//            )
//            return@execute transactionResult.data!!
//        }
//        if(transactionResult != SOAResultType.SUCCESS)
//            throw Exception(transactionResult.message)
//        return transactionResult.data!!
//    }
}

class ChallengeList(
    val challenges: List<Challenge>
): BaseObject {
    override fun toMap(): MutableMap<String, Any?> {
        var map = mutableMapOf<String, Any?>()
        map.put("challenges", challenges.map { it.toMap() })
        return map
    }
}

class ChallengeToUnsharedTransaction(val challenge: Challenge, val shareTransactionList: ShareTransactionList): BaseObject {
    override fun toMap(): MutableMap<String, Any?> {
        var map = mutableMapOf<String, Any?>()
        map.put("challenge", challenge.toMap())
        map.put("shareTransactionList", shareTransactionList.toMap())
        return map
    }
}

data class ChallengeToUnsharedTransactionNamespace(val challenge: ChallengeNamespace, val shareTransactionList: ShareTransactionListNamespace)

class ChallengeToUnsharedTransactionsList(val challengeToUnsharedTransactions: List<ChallengeToUnsharedTransaction>): BaseObject {
    override fun toMap(): MutableMap<String, Any?> {
        var map = mutableMapOf<String, Any?>()
        map.put("challengeToUnsharedTransactions", challengeToUnsharedTransactions.map { it.toMap() })
        return map
    }
}

data class ChallengeToUnsharedTransactionsNamespaceList(val challengeToUnsharedTransactions: List<ChallengeToUnsharedTransactionNamespace>)

class EmailToChallengeBalanceList(val challengeId: Int, val emailToChallengeBalances: MutableMap<String, Int>): BaseObject {
    override fun toMap(): MutableMap<String, Any?> {
        var map = mutableMapOf<String, Any?>()
        map.put("challengeId", challengeId)
        map.put("emailToChallengeBalances", emailToChallengeBalances)
        return map
    }
}

object Challenges : BaseIntIdTable("challenges") {
    val parentChallenge = reference("parent_challenge", Challenges, onDelete = ReferenceOption.CASCADE).nullable()
    val challengeSettings = reference("challenge_settings", ChallengeSettings, onDelete = ReferenceOption.CASCADE)
    val cryptoKeyPair = reference("crypto_key_pair", CryptoKeyPairs)
    val distributionFeeReward = reference("distribution_fee_reward", Rewards)
    val completionCriteria = reference("completion_criteria", CompletionCriterias)
}

class SubChallenge(id: EntityID<Int>) : BaseIntEntity(id, SubChallenges) {
    companion object : BaseIntEntityClass<SubChallenge>(SubChallenges)

    var subChallenge by SubChallenges.subChallenge
    var type by SubChallenges.type

    override fun toMap(): MutableMap<String, Any?> {
        var map = super.toMap()
        map.put("subChallengeId", idValue)
        map.put("type", type.toString())
        return map
    }
}

data class SubChallengeNamespace(val subChallengeId: Int?, val type: String?)

object ChallengeToSubChallenges : BaseIntIdTable("challenge_to_sub_challenges") {
    val challenge = reference("challenge_to_sub_challenge", Challenges, onDelete = ReferenceOption.CASCADE).primaryKey()
    val subChallenge = reference("sub_challenge_to_challenge", SubChallenges, onDelete = ReferenceOption.CASCADE).primaryKey()
}

object SubChallenges : BaseIntIdTable("sub_challenge") {
    val subChallenge = reference("sub_challenge_to_challenge", Challenges, onDelete = ReferenceOption.CASCADE).primaryKey()
    val type = enumeration("sub_challenge_type", SubChallengeType::class)
}

data class ChallengeNamespace(
    val challengeSettings: ChallengeSettingNamespace,
    val completionCriteria: CompletionCriteriaNamespace,
    val distributionFeeReward: RewardNamespace,
    val subChallenges: List<SubChallengeNamespace>? = null,
    val parentChallenge: String? = null
    )

enum class SubChallengeType(val type: String) {
    SYNC("sync"), ASYNC("async")
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
    val shareExpiration: String,
    val maxShares: Int?
) {
    fun getChallengeMetadataNamespaces(): List<MetadatasNamespace> {
        var challengeMetadatas = mutableListOf<MetadatasNamespace>()
        challengeMetadatas.add(MetadatasNamespace("challengeId", challengeId.toString()))
        challengeMetadatas.add(MetadatasNamespace("offChain", offChain.toString()))
        challengeMetadatas.add(MetadatasNamespace("shareExpiration", shareExpiration))
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
// TODO figure out if we want to use a state machine
//sealed class ChallengeState {
//    object CREATED : ChallengeState()
//    object ACTIVE : ChallengeState()
//    object COMPLETE : ChallengeState()
//    object INVALID : ChallengeState()
//    object EXPIRED : ChallengeState()
//}
//
//sealed class ChallengeEvent {
//    object OnCreated : ChallengeEvent()
//    object OnActivated : ChallengeEvent()
//    object OnCompleted : ChallengeEvent()
//    object OnInvalidated : ChallengeEvent()
//    object OnExpired : ChallengeEvent()
//}
//
//sealed class ChallengeSideEffect {
//    object LogCreated : ChallengeSideEffect()
//    object LogActivated : ChallengeSideEffect()
//    object LogCompleted : ChallengeSideEffect()
//    object LogInvalidated : ChallengeSideEffect()
//    object LogExpired : ChallengeSideEffect()
//}