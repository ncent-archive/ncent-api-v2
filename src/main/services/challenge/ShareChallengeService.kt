package main.services.challenge

import framework.models.idValue
import framework.services.DaoService
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.*
import main.services.transaction.GenerateTransactionService
import main.services.transaction.GetTransactionsService

/**
 * Share a challenge.
 */
object ShareChallengeService: SOAServiceInterface<TransactionList> {
    override fun execute(caller: Int?, params: Map<String, String>?) : SOAResult<TransactionList> {
        return DaoService.execute {
            val userAccount = UserAccount.findById(caller!!)!!
            val publicKeyToShareWith = params!!["publicKeyToShareWith"]!!
//            val keyPairToShareWith = CryptoKeyPair.find { CryptoKeyPairs.publicKey eq publicKeyToShareWith }.first()
//            val userToShareWith = UserAccount.find {
//                UserAccounts.cryptoKeyPair eq keyPairToShareWith.idValue
//            }.first()
            
            // validate users exist
            val challenge = Challenge.findById(params!!["challengeId"]!!.toInt())!!

            var shares = params!!["shares"]?.toInt()
            // if on chain -- validate the user has enough shares
            if(!challenge.challengeSettings.offChain) {
                val unsharedTransactions = GetUnsharedTransactionsService.execute(
                    caller,
                    mapOf(Pair("challengeId", challenge.idValue.toString()))
                )

                if(unsharedTransactions.result != SOAResultType.SUCCESS)
                    throw Exception(unsharedTransactions.message)

                val availableShares = unsharedTransactions.data!!.transactionsToShares.map { it.second }.sum()

                shares = shares!!.toInt()
                if(shares > availableShares)
                    throw Exception("You do not have enough shares available: $availableShares")

                // loop sharing new tx until all spent that is needed
                // TODO look into partial share -- if one tx fails what happens
                var txs = mutableListOf<Transaction>()
                var shared = 0
                unsharedTransactions.data!!.transactionsToShares.forEach {
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
                return@execute TransactionList(txs)
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
                    throw Exception(receivedTransactionResult.message)

                val txResult = GenerateTransactionService.execute(caller, TransactionNamespace(
                    from = userAccount.cryptoKeyPair.publicKey,
                    to = publicKeyToShareWith,
                    previousTransaction = receivedTransactionResult.data!!.transactions?.first()?.idValue,
                    metadatas = MetadatasListNamespace(
                        ChallengeMetadata(
                            challenge.idValue,
                            challenge.challengeSettings.offChain,
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
                    throw Exception(txResult.message)
                return@execute TransactionList(listOf(txResult.data!!))
            }
        }
    }
}