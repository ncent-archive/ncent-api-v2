package main.services.challenge

import framework.models.idValue
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import main.helpers.ChallengeHelper
import main.services.transaction.GetProvidenceChainsService
import main.services.transaction.GetTransactionsService
import org.jetbrains.exposed.sql.select

object GetChainsForChallengeService {
    fun execute(caller: UserAccount, challengeId: Int): SOAResult<MutableList<MutableList<String>>> {
        val challenge = ChallengeHelper.findChallengeById(challengeId)
        val transaction = getOriginatingTransaction(challenge)
        if(transaction == null)
            return SOAResult(SOAResultType.FAILURE, "Failed to find a correct transaction for this challenge.")

        val chainsTransactionsResult = GetProvidenceChainsService.execute(transaction)
        if(chainsTransactionsResult.data == null || chainsTransactionsResult.data!!.isEmpty())
            return SOAResult(SOAResultType.FAILURE, "Failed to find any providence chains for this challenge.")

        val chains = chainsTransactionsResult.data!!

        val toPublicKeys = chains.map { txList -> txList.transactions.map { tx -> tx.to ?: "" } }
        val userAccountsMap = getUserAccountsByPublicKeys(toPublicKeys.flatten())

        var resultEmails = mutableListOf<MutableList<String>>()
        chains.forEach { txs ->
            var resultEmailsForChain = mutableListOf<String>()
            txs.transactions.forEach { tx ->
                val email = userAccountsMap[tx.to]!!.userMetadata.email
                resultEmailsForChain.add(email)
            }
            resultEmails.add(resultEmailsForChain)
        }
        return SOAResult(SOAResultType.SUCCESS,null, resultEmails)
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

    private fun getUserAccountsByPublicKeys(publicKeys: List<String>): Map<String, UserAccount> {
        val query = UserAccounts
            .innerJoin(CryptoKeyPairs)
            .select {
                CryptoKeyPairs.publicKey inList publicKeys
            }
        val userAccounts = UserAccount.wrapRows(query).toList()
        return userAccounts.associate { userAccount -> userAccount.cryptoKeyPair.publicKey to userAccount }
    }
}
