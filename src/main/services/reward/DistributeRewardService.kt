package main.services.reward

import framework.services.DaoService
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.CompletionCriteria
import main.daos.CompletionCriterias
import main.daos.Reward
import main.daos.Transaction
import main.helpers.TransferTokenHelper

/**
 * Transfer tokens based on rewards
 */
class DistributeRewardService: SOAServiceInterface<Transaction> {
    override fun execute(caller: Int?, params: Map<String, String>?) : SOAResult<Transaction> {
        return DaoService<Transaction>().execute {
            val reward = Reward.findById(params!!["reward_id"]!!.toInt())!!
            val address = CompletionCriteria.find {
                CompletionCriterias.reward eq reward.id
            }.first().address
            val tx = Transaction.findById(params!!["transaction_id"]!!.toInt())!!

            // calculate rewards
            // get all the transactions -- verify they have not been spent
            // check that there are no outbount tx from the completion criteria -- if there are deduct

            val helper = TransferTokenHelper()
            val transactionsResult = helper.getTransferHistory(address, null)
            if(transactionsResult.result != SOAResultType.SUCCESS)
                throw Exception(transactionsResult.message)

            val mapOfTransfers = helper.getMapOfTransfersByCurrency(transactionsResult.data!!)
            val mapOfBalances = helper.getMapOfBalancesByCurrency(address, mapOfTransfers)

            //TODO what should we do if any of the balances are negative but some are positive?
            // for now we will just distribute
            return@execute tx
        }
    }
}