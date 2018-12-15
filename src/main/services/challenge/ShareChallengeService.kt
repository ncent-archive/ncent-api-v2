//package main.services.challenge
//
//import framework.models.idValue
//import framework.services.DaoService
//import kotlinserverless.framework.services.SOAResult
//import kotlinserverless.framework.services.SOAResultType
//import kotlinserverless.framework.services.SOAServiceInterface
//import main.daos.*
//import main.services.transaction.GenerateTransactionService
//
///**
// * Share a challenge.
// */
//object ShareChallengeService: SOAServiceInterface<Transaction> {
//    override fun execute(caller: Int?, params: Map<String, String>?) : SOAResult<Transaction> {
//        return DaoService.execute {
//            val userAccount = UserAccount.findById(caller!!)!!
//            val userToShareWith = UserAccount.find {
//
//            }.first()
//            val challenge = Challenge.findById(params!!["challengeId"]!!.toInt())!!
//
//            val shares = params!!["shares"]
//            // if on chain -- validate the user has enough shares
//            if(!challenge.challengeSettings.offChain) {
//                val unsharedTransactions = GetUnsharedTransactionsService.execute(
//                    caller,
//                    mapOf(
//                        Pair("challengeId", challenge.idValue.toString())
//                    )
//                )
//            }
//            // TODO should we consider previous tx as a list?
//            // TODO currently will use first come first serve as the previous tx
//            val txResult = GenerateTransactionService.execute(caller, TransactionNamespace(
//                from = userAccount.cryptoKeyPair.publicKey,
//                to = userToShareWith.cryptoKeyPair.publicKey,
//                previousTransaction = null,
//                metadatas = MetadatasListNamespace(
//                    ChallengeMetadata(
//                        challenge.idValue,
//                        challenge.challengeSettings.offChain,
//                        challenge.challengeSettings.maxShares
//                    ).getChallengeMetadataNamespaces()
//                ),
//                action = ActionNamespace(
//                    type = ActionType.SHARE,
//                    data = challenge.idValue,
//                    dataType = Challenge::class.simpleName!!
//                )
//            ), null)
//
//            if(txResult.result != SOAResultType.SUCCESS)
//                throw Exception(txResult.message)
//            return@execute txResult.data!!
//        }
//    }
//}