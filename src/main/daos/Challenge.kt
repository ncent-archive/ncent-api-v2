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

    var challengeSettings by Challenges.challengeSettings
    // TODO: change to user referrersOn
    var asyncSubChallenges by Challenges.asyncSubChallenges
    // TODO: change to user referrersOn
    var syncSubChallenges by Challenges.syncSubChallenges
    var resultVectors by Challenges.resultVectors
}

object Challenges : BaseIntIdTable("challenges") {
    val challengeSettings = reference("challenge_settings", ChallengeSettings)
    val asyncSubChallenges = reference("challenges", Challenges)
    val syncSubChallenges = reference("challenges", Challenges)
    val resultVectors = reference("result_vectors", ResultVectors)
}