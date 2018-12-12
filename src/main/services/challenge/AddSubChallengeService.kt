package main.services.challenge

import framework.services.DaoService
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.*
import main.services.completion_criteria.GenerateCompletionCriteriaService
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import java.lang.Exception

/**
 * Add a sub challenge to a challenge.
 */
object AddSubChallengeService: SOAServiceInterface<SubChallenge> {
    override fun execute(caller: Int?, d: Any?, params: Map<String, String>?) : SOAResult<SubChallenge> {
        val subChallengeNamespace = d!! as ChallengeNamespace
        return DaoService.execute {
            // TODO validate the sub challenge expiration, amounts, same parent id, etc...must accomidate for parent challenge
            val subChallengeResult = GenerateChallengeService.execute(caller, subChallengeNamespace, null)
            if(subChallengeResult.result != SOAResultType.SUCCESS)
                throw Exception(subChallengeResult.message)

            val challengeFound = Challenge.findById(params!!["challengeId"]!!.toInt())!!

            val subChallengeId = SubChallenges.insertAndGetId {
                it[subChallenge] = subChallengeResult.data!!.id
                it[type] = SubChallengeType.valueOf(params!!["subChallengeType"]!!)
            }

            ChallengeToSubChallenges.insert {
                it[challenge] = challengeFound.id
                it[subChallenge] = subChallengeId
            }

            return@execute SubChallenge.findById(subChallengeId)!!
        }
    }
}