package main.services.challenge

import framework.models.idValue
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import main.services.transaction.GenerateTransactionService

/**
 * Trigger a challenge state change.
 */
object ChangeChallengeStateService: SOAServiceInterface<Transaction> {
    override fun execute(caller: UserAccount, params: Map<String, String>?) : SOAResult<Transaction> {
        val challenge = Challenge.findById(params!!["challengeId"]!!.toInt())!!
        if(challenge.challengeSettings.admin != caller.id)
            return SOAResult(SOAResultType.FAILURE, "This user cannot change the challenge state")
        var newState = ActionType.valueOf(params!!["state"]!!)
        val oldTx = challenge.getLastStateChangeTransaction()!!
        val oldState = oldTx.action.type

        if(!challenge.canTransitionState(oldState, newState))
            return SOAResult(SOAResultType.FAILURE, "Cannot transition from ${oldState.type} to ${newState.type}")

        if(challenge.shouldExpire() && challenge.canTransitionState(oldState, ActionType.EXPIRE)) {
            newState = ActionType.EXPIRE
        }
        return GenerateTransactionService.execute(caller, TransactionNamespace(
            from = challenge.cryptoKeyPair.publicKey,
            to = challenge.cryptoKeyPair.publicKey,
            previousTransaction = oldTx.idValue,
            metadatas = null,
            action = ActionNamespace(
                type = newState,
                data = challenge.idValue,
                dataType = Challenge::class.simpleName!!
            )
        ), null)
    }
}