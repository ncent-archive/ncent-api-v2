package main.services.completion_criteria

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.CompletionCriteria

/**
 * Generate a new completion criteria
 */
object GenerateCompletionCriteriaService: SOAServiceInterface<CompletionCriteria> {
    override fun execute(caller: Int?, data: Any?, params: Map<String, String>?) : SOAResult<CompletionCriteria> {
        throw NotImplementedError()
    }
}