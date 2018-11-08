package main.daos

import framework.models.BaseIntEntity
import framework.models.BaseIntEntityClass
import framework.models.BaseIntIdTable
import org.jetbrains.exposed.dao.*

/**
 * Used to represent challenges and store pointers to models that
 * store the state for a challenge over the lifecycle of that challenge
 *
 * @property id
 * @property challengeSettings ChallengeSettings
 * @property asyncSubChallenges sub challenges that can be completed in any order
 * @property syncSubChallenges sub challenges that must be completed in order
 * @property resultVectors ResultVectors
 */
class Challenge(id: EntityID<Int>) : BaseIntEntity(id, Challenges) {
    companion object : BaseIntEntityClass<Challenge>(Challenges)

    var challengeSettings by ChallengeSetting referencedOn Challenges.challengeSettings
    var asyncSubChallenges by Challenge via SubChallenges
    var syncSubChallenges by Challenge via SubChallenges
    var resultVectors by ResultVector via ResultVectors
}

object Challenges : BaseIntIdTable("challenges") {
    val challengeSettings = reference("challenge_settings", ChallengeSettings)
}

object SubChallenges : BaseIntIdTable("sub_challenges") {
    val parentChallenge = reference("parent_challenge", Challenges).primaryKey(0)
    val subChallenge = reference("sub_challenge", Challenges).primaryKey(1)
    val type = enumeration("sub_challenge_type", SubChallengeType::class)
}

enum class SubChallengeType {
    SYNC, ASYNC
}