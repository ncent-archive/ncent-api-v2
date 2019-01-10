package main.services.challenge

import framework.models.idValue
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import main.services.transaction.GenerateTransactionService
import main.services.transaction.GetTransactionsService

/**
 * Share a challenge.
 */
object ShareChallengeService: SOAServiceInterface<TransactionList> {
    override fun execute(caller: Int?, params: Map<String, String>?) : SOAResult<TransactionList> {
        val userAccount = UserAccount.findById(caller!!)!!
        val publicKeyToShareWith = params!!["publicKeyToShareWith"]!!
//            val keyPairToShareWith = CryptoKeyPair.find { CryptoKeyPairs.publicKey eq publicKeyToShareWith }.first()
//            val userToShareWith = UserAccount.find {
//                UserAccounts.cryptoKeyPair eq keyPairToShareWith.idValue
//            }.first()

        // validate users exist
        val challenge = Challenge.findById(params!!["challengeId"]!!.toInt())!!

        var shares = params!!["shares"]?.toInt()
        // TODO look into using a datetime formatter
        val expiration = params!!["expiration"] ?: challenge.challengeSettings.shareExpiration.toString()

        // if on chain -- validate the user has enough shares
        if(!challenge.challengeSettings.offChain) {
            val sharabilityResult = ValidateShareService.execute(caller, mapOf(
                Pair("challengeId", challenge.idValue.toString()),
                Pair("shares", shares!!.toString())
            ))
            if(sharabilityResult.result != SOAResultType.SUCCESS || !sharabilityResult.data!!.first)
                return SOAResult(SOAResultType.FAILURE, sharabilityResult.message)

            val unsharedTransactions = sharabilityResult.data!!.second!!

            // loop sharing new tx until all spent that is needed
            // TODO look into partial share -- if one tx fails what happens
            var txs = mutableListOf<Transaction>()
            var shared = 0
            unsharedTransactions.transactionsToShares.forEach {
                if(shared >= shares)
                    return@forEach
                val ustx = it.first
                val amount = it.second

                val txResult = GenerateTransactionService.execute(caller, TransactionNamespace(
                    from = userAccount.cryptoKeyPair.publicKey,
                    to = publicKeyToShareWith,
                    previousTransaction = ustx.idValue,
                    metadatas = MetadatasListNamespace(
                        ChallengeMetadata(
                            challenge.idValue,
                            challenge.challengeSettings.offChain,
                            expiration,
                            Math.min(shares, amount)
                        ).getChallengeMetadataNamespaces()
                    ),
                    action = ActionNamespace(
                        type = ActionType.SHARE,
                        data = challenge.idValue,
                        dataType = Challenge::class.simpleName!!
                    )
                ), null)

                // TODO decide what to do if the txresult fails
                txs.add(txResult.data!!)
                shared += amount
            }
            return SOAResult(SOAResultType.SUCCESS, null, TransactionList(txs))
        } else {
            // create a tx using previous as the first tx
            val receivedTransactionResult = GetTransactionsService.execute(
                caller,
                mapOf(
                    Pair("to", userAccount.cryptoKeyPair.publicKey),
                    Pair("dataType", "Challenge"),
                    Pair("data", params!!["challengeId"]!!),
                    Pair("type", "SHARE")
                )
            )

            if(receivedTransactionResult.result != SOAResultType.SUCCESS)
                return SOAResult(SOAResultType.FAILURE, receivedTransactionResult.message)

            val previousTx = receivedTransactionResult.data!!.transactions?.first()

            val txResult = GenerateTransactionService.execute(caller, TransactionNamespace(
                from = userAccount.cryptoKeyPair.publicKey,
                to = publicKeyToShareWith,
                previousTransaction = previousTx?.idValue,
                metadatas = MetadatasListNamespace(
                    ChallengeMetadata(
                        challenge.idValue,
                        challenge.challengeSettings.offChain,
                        expiration,
                        shares ?: challenge.challengeSettings.maxSharesPerReceivedShare
                    ).getChallengeMetadataNamespaces()
                ),
                action = ActionNamespace(
                    type = ActionType.SHARE,
                    data = challenge.idValue,
                    dataType = Challenge::class.simpleName!!
                )
            ), null)

            if(txResult.result != SOAResultType.SUCCESS)
                return SOAResult(SOAResultType.FAILURE, txResult.message)
            return SOAResult(SOAResultType.SUCCESS, null, TransactionList(listOf(txResult.data!!)))
        }
    }
}