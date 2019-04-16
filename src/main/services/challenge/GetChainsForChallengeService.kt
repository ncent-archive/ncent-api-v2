package main.services.challenge

import framework.models.idValue
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import main.helpers.ChallengeHelper
import main.services.transaction.GetProvidenceChainService
import main.services.transaction.GetTransactionsService
import org.jetbrains.exposed.sql.select

object GetChainsForChallengeService {
    fun execute(caller: UserAccount, challengeId: Int): SOAResult<Challenger<UserAccount>> {
        val challenge = ChallengeHelper.findChallengeById(challengeId)
        val transaction = getOriginatingTransaction(challenge)
        if(transaction == null)
            return SOAResult(SOAResultType.FAILURE, "Failed to find a correct transaction for this challenge.")

        val challengers = getChildrenGraph(transaction)

        return SOAResult(SOAResultType.SUCCESS,null, challengers)
    }

    private fun getChildrenGraph(currentTransaction: Transaction): Challenger<UserAccount> {
        var children = GetProvidenceChainService.getChildren(currentTransaction.id).toMutableList()
        var childrenGraph = mutableListOf<Challenger<UserAccount>>()
        while(children.any()) {
            val currentChild = children.removeAt(0)
            val currentChildChildrenGraph = getChildrenGraph(currentChild)
            childrenGraph.add(currentChildChildrenGraph)
        }
        return Challenger(getUserAccountByPublicKey(currentTransaction.to!!), childrenGraph)
    }

    private fun getOriginatingTransaction(challenge: Challenge): Transaction? {
        val transactionsListResult = GetTransactionsService.execute(
            from = challenge.cryptoKeyPair.publicKey,
            to = challenge.challengeSettings.admin.cryptoKeyPair.publicKey,
            previousTxId = null,
            actionNamespace = ActionNamespace(
                type = ActionType.SHARE,
                data = challenge.idValue,
                dataType = Challenge::class.simpleName!!
            )
        )

        if(transactionsListResult?.data?.transactions?.count() == 1)
            return transactionsListResult.data!!.transactions.first()
        return null
    }

    private fun getUserAccountByPublicKey(publicKey: String): UserAccount {
        val query = UserAccounts
            .innerJoin(CryptoKeyPairs)
            .select {
                CryptoKeyPairs.publicKey eq publicKey
            }
        return UserAccount.wrapRows(query).toList().first()
    }
}
