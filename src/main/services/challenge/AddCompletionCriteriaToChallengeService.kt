package main.services.challenge

import framework.services.DaoService
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.*
import main.services.completion_criteria.GenerateCompletionCriteriaService
import org.jetbrains.exposed.sql.insert
import java.lang.Exception

/**
 * Add a completion criteria to a challenge.
 */
object AddCompletionCriteriaToChallengeService: SOAServiceInterface<CompletionCriteria> {
    override fun execute(caller: Int?, d: Any?, params: Map<String, String>?) : SOAResult<CompletionCriteria> {
        return DaoService.execute {
            val completionCriteriaResult = GenerateCompletionCriteriaService.execute(caller, d, null)
            if(completionCriteriaResult.result != SOAResultType.SUCCESS)
                throw Exception(completionCriteriaResult.message)

            val challengeFound = Challenge.findById(params!!["challengeId"]!!.toInt())!!
            ChallengeToCompletionCriterias.insert {
                it[challenge] = challengeFound.id
                it[completionCriteria] = completionCriteriaResult.data!!.id
            }

            return@execute completionCriteriaResult.data!!
        }
    }
}