package main.services.completion_criteria

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.CompletionCriteria
import main.daos.UserAccount

/**
 * Update the completion criteria address if the caller is the existing criteria address
 */
object ChangeCompletionCriteriaService: SOAServiceInterface<CompletionCriteria> {
    override fun execute(caller: Int?, params: Map<String, String>?) : SOAResult<CompletionCriteria> {
        val cc = CompletionCriteria.findById(params!!["completion_criteria_id"]!!.toInt())!!
        if(UserAccount.findById(caller!!)!!.cryptoKeyPair.publicKey != cc.address)
            return SOAResult(SOAResultType.FAILURE, "Only the current completion criteria address can alter the completion critera")
        // TODO may want to validate this address exists
        cc.address = params!!["new_completion_criteria_address"]!!
        return SOAResult(SOAResultType.SUCCESS, null, cc)
    }
}