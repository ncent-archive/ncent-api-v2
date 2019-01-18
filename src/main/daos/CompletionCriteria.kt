package main.daos

import framework.models.BaseIntEntity
import framework.models.BaseIntEntityClass
import framework.models.BaseIntIdTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.Table
import org.joda.time.DateTime

/**
 * Currently only housing who can validate the completion of a challenge
 * Eventually this will house things like smart contract completers.
 *
 * @property id
 * @property address The address which can trigger validation of completion
 * @property reward the reward amount and it's pool
 * @property prereq If this completion criteria can only be completed if another is completed
 */
class CompletionCriteria(id: EntityID<Int>) : BaseIntEntity(id, CompletionCriterias) {
    companion object : BaseIntEntityClass<CompletionCriteria>(CompletionCriterias)

    var address by CompletionCriterias.address
    var reward by Reward referencedOn CompletionCriterias.reward
    var prereq by Challenge via PrerequisiteChallenge

    override fun toMap(): MutableMap<String, Any?> {
        var map = super.toMap()
        map.put("address", address)
        map.put("reward", reward?.toMap())
        map.put("prereq", prereq.map { it.toMap() })
        return map
    }
}

object PrerequisiteChallenge : Table("prerequisite_challenge") {
    var completionCriteria = reference("completion_criteria_to_prereq", CompletionCriterias).primaryKey()
    var prereqChallenge = reference("prereq_to_completion_criteria", Challenges).primaryKey()
}

object CompletionCriterias : BaseIntIdTable("completion_criterias") {
    val address = varchar("address", 256)
    val reward = reference("reward", Rewards)
}

data class CompletionCriteriaNamespace(val address: String?, val reward: RewardNamespace, val prereq: List<Int> = listOf())