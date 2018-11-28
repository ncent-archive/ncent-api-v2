package main.services.reward

import framework.services.DaoService
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.*
import main.helpers.TransferTokenHelper
import main.services.transaction.GetProvidenceChainService

/**
 * Transfer tokens based on rewards
 */
class DistributeRewardService: SOAServiceInterface<TransactionList> {

    private val transferTokenHelper = TransferTokenHelper()
    private val daoService = DaoService<TransactionList>()
    private val providenceChainService = GetProvidenceChainService()

    override fun execute(caller: Int?, params: Map<String, String>?) : SOAResult<TransactionList> {
        return daoService.execute {
            val reward = Reward.findById(params!!["reward_id"]!!.toInt())!!
            val address = CompletionCriteria.find {
                CompletionCriterias.reward eq reward.id
            }.first().address

            // calculate rewards
            // get all the transactions -- verify they have not been spent
            // check that there are no outbount tx from the completion criteria -- if there are deduct

            val transactionsResult = transferTokenHelper.getTransferHistory(address, null)
            if(transactionsResult.result != SOAResultType.SUCCESS)
                throw Exception(transactionsResult.message)

            val mapOfTransfers = transferTokenHelper.getMapOfTransfersByCurrency(transactionsResult.data!!)
            val mapOfBalances = transferTokenHelper.getMapOfBalancesByCurrency(address, mapOfTransfers)

            //TODO what should we do if any of the balances are negative but some are positive?
            //TODO handle 'ALL' type reward audience -- get all chains
            val providenceChainResult = providenceChainService.execute(caller, params!!["transaction_id"]!!.toInt())
            if(providenceChainResult.result != SOAResultType.SUCCESS)
                throw Exception(providenceChainResult.message)

            var resultingTransactions = mutableListOf<Transaction>()
            // for now we will just distribute
            mapOfBalances.forEach { tokenId, balance ->
                if(balance <= 0.0)
                    return@forEach
                val resultingTxs = transferRewardsToChain(reward.type.type, tokenId, balance, providenceChainResult.data!!, address)
                resultingTransactions.addAll(resultingTxs)
            }
            return@execute TransactionList(resultingTransactions)
        }
    }

//    private fun transferRewardsToAll(rewardTypeName: RewardTypeName, tokenId: Int, balance: Double, providenceChain: TransactionList): Transaction? {
//        // TODO -- handle ALL type reward audience
//    }

    private fun transferRewardsToChain(
        rewardTypeName: RewardTypeName,
        tokenId: Int,
        balance: Double,
        providenceChain: TransactionList,
        address: String
    ): List<Transaction> {
        var resultingTxs = mutableListOf<Transaction>()
        when(rewardTypeName) {
            RewardTypeName.EVEN -> {
                // distribute reward evenly to everyone in the providence chain list
                val size = providenceChain.transactions.size
                val amount = balance / size
                providenceChain.transactions.forEach { tx ->
                    // TODO Add error handling for transfering.
                    resultingTxs.add(transferTokenHelper.transferToken(
                        null,
                        address,
                        tx.to!!,
                        tokenId,
                        amount,
                        ActionType.PAYOUT,
                        null,
                        "Reward distribution"
                    ).data!!)
                }
            }
            RewardTypeName.SINGLE -> {
                // distribute reward to first person in providence chain list
                val tx = providenceChain.transactions.first()
                // TODO Add error handling for transfering.
                resultingTxs.add(transferTokenHelper.transferToken(
                    null,
                    address,
                    tx.to!!,
                    tokenId,
                    balance,
                    ActionType.PAYOUT,
                    null,
                    "Reward distribution"
                ).data!!)
            }
            RewardTypeName.LOGARITHMIC -> {
                // distribute reward in logarithmic pattern
            }
            RewardTypeName.EXPONENTIAL -> {
                // distribute reward in exponential pattern
            }
            RewardTypeName.N_OVER_2 -> {
                // distribute reward in n/2 pattern
                var n_over_2 = balance / 2
                providenceChain.transactions.forEach { tx ->
                    // TODO Add error handling for transfering.
                    resultingTxs.add(transferTokenHelper.transferToken(
                            null,
                            address,
                            tx.to!!,
                            tokenId,
                            n_over_2,
                            ActionType.PAYOUT,
                            null,
                            "Reward distribution"
                    ).data!!)
                    n_over_2 /= 2
                }
            }
        }
        return resultingTxs
    }
}
