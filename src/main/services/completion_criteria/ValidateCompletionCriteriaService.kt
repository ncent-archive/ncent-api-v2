package main.services.completion_criteria

import framework.services.DaoService
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.CompletionCriteria
import main.daos.UserAccount

/**
 * For the time being, just validate the caller is the criteria address
 */
object ValidateCompletionCriteriaService: SOAServiceInterface<Boolean> {
    override fun execute(caller: Int?, params: Map<String, String>?) : SOAResult<Boolean> {
        return DaoService.execute {
            val cc = CompletionCriteria.findById(params!!["completion_criteria_id"]!!.toInt())!!
            return@execute UserAccount.findById(caller!!)!!.cryptoKeyPair.publicKey == cc.address
        }
    }
}