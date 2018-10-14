package main.services.completion_criteria

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.CompletionCriteria

/**
 * Update the completion criteria address if the caller is the existing criteria address
 */
class ChangeCompletionCriteriaService: SOAServiceInterface<CompletionCriteria> {
    override fun execute(caller: Int?, data: CompletionCriteria?, params: Map<String, String>?) : SOAResult<CompletionCriteria> {
        throw NotImplementedError()
    }
}