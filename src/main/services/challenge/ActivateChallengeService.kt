package main.services.challenge

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.*

/**
 * Trigger a challenge state change to active.
 */
object ActivateChallengeService: SOAServiceInterface<Transaction> {
    override fun execute(caller: Int?, params: Map<String, String>?) : SOAResult<Transaction> {
        var newParams = mutableMapOf<String,String>()
        if(params!!["state"] != null)
            return SOAResult(SOAResultType.FAILURE, "Cannot pass state to activate service.")
        newParams.putAll(params!!)
        newParams["state"] = "ACTIVATE"
        return ChangeChallengeStateService.execute(caller, newParams)
    }
}