package main.services.completion_criteria

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.CompletionCriteria
import main.daos.UserAccount

/**
 * Update the completion criteria address if the caller is the existing criteria address
 */
object ChangeCompletionCriteriaService {
    fun execute(caller: UserAccount, completionCriteriaId: Int, newCompletionCriteriaAddress: String) : SOAResult<CompletionCriteria> {
        val cc = CompletionCriteria.findById(completionCriteriaId)!!
        if(caller.cryptoKeyPair.publicKey != cc.address)
            return SOAResult(SOAResultType.FAILURE, "Only the current completion criteria address can alter the completion critera")
        // TODO may want to validate this address exists
        cc.address = newCompletionCriteriaAddress
        return SOAResult(SOAResultType.SUCCESS, null, cc)
    }
}