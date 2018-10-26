package main.services.completion_criteria

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.CompletionCriteria

/**
 * Generate a new completion criteria
 */
class GenerateCompletionCriteriaService: SOAServiceInterface<CompletionCriteria> {
    override fun execute(caller: Int?, data: CompletionCriteria?, params: Map<String, String>?) : SOAResult<CompletionCriteria> {
        throw NotImplementedError()
    }
}