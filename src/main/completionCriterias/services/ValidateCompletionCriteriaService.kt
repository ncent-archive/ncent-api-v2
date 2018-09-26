package main.completionCriterias.services

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.completionCriterias.models.CompletionCriteria

/**
 * For the time being, just validate the caller is the criteria address
 */
class ValidateCompletionCriteriaService: SOAServiceInterface<CompletionCriteria> {
    override fun execute(caller: Int?, data: CompletionCriteria?, params: Map<String, String>?) : SOAResult<CompletionCriteria> {
        throw NotImplementedError()
    }
}