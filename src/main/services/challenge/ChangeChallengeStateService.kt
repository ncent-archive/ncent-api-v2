package main.services.challenge

import framework.models.idValue
import framework.services.DaoService
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.*
import main.services.transaction.GenerateTransactionService

/**
 * Trigger a challenge state change.
 */
object ChangeChallengeStateService: SOAServiceInterface<Transaction> {
    override fun execute(caller: Int?, params: Map<String, String>?) : SOAResult<Transaction> {
        return DaoService.execute {
            val userAccount = UserAccount.findById(caller!!)!!
            val challenge = Challenge.findById(params!!["challengeId"]!!.toInt())!!
            if(challenge.challengeSettings.admin != userAccount.id)
                throw Exception("This user cannot change the challenge state")
            val newState = ActionType.valueOf(params!!["state"]!!)
            val oldTx = challenge.getLastStateChangeTransaction()!!
            val oldState = oldTx.action.type

            if(!challenge.canTransitionState(oldState, newState))
                throw Exception("Cannot transition from ${oldState.type} to ${newState.type}")

            val txResult = GenerateTransactionService.execute(caller, TransactionNamespace(
                from = challenge.cryptoKeyPair.publicKey,
                to = userAccount.cryptoKeyPair.publicKey,
                previousTransaction = oldTx.idValue,
                metadatas = null,
                action = ActionNamespace(
                    type = newState,
                    data = challenge.idValue,
                    dataType = Challenge::class.simpleName!!
                )
            ), null)

            if(txResult.result != SOAResultType.SUCCESS)
                throw Exception(txResult.message)
            return@execute txResult.data!!
        }
    }
}