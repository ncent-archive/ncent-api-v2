package main.daos

import framework.models.BaseIntEntity
import framework.models.BaseIntEntityClass
import framework.models.BaseIntIdTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.Table

/**
 * Currently only housing who can validate the completion of a challenge
 * Eventually this will house things like smart contract completers.
 *
 * @property id
 * @property address The address which can trigger validation of completion
 * @property reward the reward amount and it's pool
 * @property expiration
 * @property prereq If this completion criteria can only be completed if another is completed
 */
class CompletionCriteria(id: EntityID<Int>) : BaseIntEntity(id, CompletionCriterias) {
    companion object : BaseIntEntityClass<CompletionCriteria>(CompletionCriterias)

    var cryptoKeyPair by CryptoKeyPair referencedOn CompletionCriterias.cryptoKeyPair
    var reward by CompletionCriterias.reward
    var expiration by CompletionCriterias.expiration
    var prereq by CompletionCriteria via PrerequisiteCompletionCriterias
}

object PrerequisiteCompletionCriterias : Table("prerequisite_completion_criterias") {
    var completionCriteria = reference("completion_criteria_to_prereq", CompletionCriterias).primaryKey()
    var prereqCompletionCriteria = reference("prereq_to_completion_criteria", CompletionCriterias).primaryKey()
}

object CompletionCriterias : BaseIntIdTable("completion_criterias") {
    val reward = reference("reward", Rewards)
    val expiration = datetime("expiration")
    val cryptoKeyPair = reference("crypto_key_pair", CryptoKeyPairs)
}