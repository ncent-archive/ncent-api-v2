Table of Contents

1. [User APIs](#user-apis)
   + [retrieveAllUsers](#retrieveallusers)
   + [retrieveUser](#retrieveuser)
   + [createUser](#createuser)
   + [updateUser](#updateuser)
   + [deleteUser](#deleteuser)
1. [Session APIs](#session-apis)
   + [createUserSession](#createusersession)
   + [deleteUserSession](#deleteusersession)
1. [Challenge APIs](#challenge-apis)
   + [retrieveAllChallenges](#retrieveallchallenges)
   + [retrieveAllChallengesForUser](#retrieveallchallengesforuser)
   + [retrieveChallenge](#retrievechallenge)
   + [modifyChallengeState](#modifychallengestate)
   + [shareChallenge](#sharechallenge)
   + [endChallenge](#endchallenge)
   + [redeemChallenge](#redeemchallenge)
1. [ChallengeSetting APIs](#challengesetting-apis)
   + [retrieveChallengeSettings](#retrievechallengesettings)
1. [CompletionCriteria APIs](#completioncriteria-apis)
   + [retrieveCompletionCriteria](#retrievecompletioncriteria)
1. [Reward APIs](#reward-apis)
   + [retrieveReward](#retrievereward)
1. [RewardPool APIs](#rewardpool-apis)
   + [retrieveRewardPool](#retrieverewardpool)
   + [addToRewardPool](#addtorewardpool)
1. [RewardType APIs](#rewardtype-apis)
   + [retrieveRewardType](#retrieverewardtype)
1. [Token APIs](#token-apis)
   + [retrieveAllTokens](#retrievealltokens)
   + [retrieveToken](#retrievetoken)
   + [sendTokens](#sendtokens)
1. [Transaction APIs](#transaction-apis)
   + [retrieveAllTransactions](#retrievealltransactions)
   + [retrieveAllTransactionsForUser](#retrievealltransactionsforuser)
   + [retrieveAllTransactionsForChallenge](#retrievealltransactionsforchallenge)
   + [retrieveProvenanceChainFIFO](#retrieveprovenancechainfifo)
   + [createTransaction](#createtransaction)
   
# User APIs

The following APIs will interact with the UserAccountsController.

## retrieveAllUsers

## findOne

This API is a simple retrieval for a single user account

#### Route

/user_accounts

#### Method

GET

#### Parameters

1. user
   * The user making the API call
   * Datatype: UserAccount
1. id
   * The ID of the user to be retrieved
   * Datatype: Int
   
#### Sample Response
```json
{
   "createdAt":"2019-01-06T14:29:19.561-08:00",
   "updatedAt":"null",
   "deletedAt":"null",
   "userMetadata":{
      "createdAt":"2019-01-06T14:29:19.556-08:00",
      "updatedAt":"null",
      "deletedAt":"null",
      "email":"dev0@ncnt.io",
      "firstname":"dev0",
      "lastname":"ncnt0",
      "metadatas":[

      ]
   },
   "cryptoKeyPair":{
      "createdAt":"2019-01-06T14:29:19.558-08:00",
      "updatedAt":"null",
      "deletedAt":"null",
      "publicKey":"[B@3ce7cd16"
   },
   "apiCreds":{
      "createdAt":"2019-01-06T14:29:19.559-08:00",
      "updatedAt":"null",
      "deletedAt":"null",
      "apiKey":"[B@2b72c09b"
   },
   "session":{
      "createdAt":"2019-01-06T14:29:19.560-08:00",
      "updatedAt":"null",
      "deletedAt":"null",
      "sessionKey":"80900064-0c68-45b5-988b-82df5ba55419",
      "expiration":"2019-01-07T14:29:19.549-08:00"
   }
}
```

## create

This API is used for the creation of a new User instance

#### Route

/user_accounts

#### Method

POST 

#### Parameters 

1. user
   * The user making the API call
   * Datatype: UserAccount
1. params
   * The intended attributes for the new user to be created
   * Datatype: Map<String, String>

#### Sample Response
```json
{  
   "value":"{\"createdAt\":\"2019-01-06T23:39:12.345Z\",\"updatedAt\":\"null\",\"deletedAt\":\"null\",\"userMetadata\":{\"createdAt\":\"2019-01-06T15:39:12.278-08:00\",\"updatedAt\":\"null\",\"deletedAt\":\"null\",\"email\":\"dev@ncnt.io\",\"firstname\":\"dev\",\"lastname\":\"ncnt\",\"metadatas\":[]},\"cryptoKeyPair\":{\"createdAt\":\"2019-01-06T15:39:12.340-08:00\",\"updatedAt\":\"null\",\"deletedAt\":\"null\",\"publicKey\":\"[B@6efb3ebb\"},\"apiCreds\":{\"createdAt\":\"2019-01-06T15:39:12.341-08:00\",\"updatedAt\":\"null\",\"deletedAt\":\"null\",\"apiKey\":\"[B@725c7316\"},\"session\":{\"createdAt\":\"2019-01-06T15:39:12.342-08:00\",\"updatedAt\":\"null\",\"deletedAt\":\"null\",\"sessionKey\":\"5cdd7f1c-5331-4a89-8ab7-d1d20df53fed\",\"expiration\":\"2019-01-07T15:39:12.193-08:00\"}}",
   "privateKey":"[C@6364d579",
   "secretKey":"[C@4a0e0019"
}
```

## updateUser

## deleteUser

## login

This API is called in order to begin a session for a user account

#### Route

/user_accounts/login

#### Method

PATCH

#### Parameters

1. user
   * The user making the API call
   * Datatype: UserAccount
1. request
   * The incoming request
   * Datatype: Request
   
#### Sample Response

```json
{
   "createdAt":"2019-01-06T14:29:19.561-08:00",
   "updatedAt":"null",
   "deletedAt":"null",
   "userMetadata":{
      "createdAt":"2019-01-06T14:29:19.556-08:00",
      "updatedAt":"null",
      "deletedAt":"null",
      "email":"dev0@ncnt.io",
      "firstname":"dev0",
      "lastname":"ncnt0",
      "metadatas":[

      ]
   },
   "cryptoKeyPair":{
      "createdAt":"2019-01-06T14:29:19.558-08:00",
      "updatedAt":"null",
      "deletedAt":"null",
      "publicKey":"[B@3ce7cd16"
   },
   "apiCreds":{
      "createdAt":"2019-01-06T14:29:19.559-08:00",
      "updatedAt":"null",
      "deletedAt":"null",
      "apiKey":"[B@2b72c09b"
   },
   "session":{
      "createdAt":"2019-01-06T14:29:19.560-08:00",
      "updatedAt":"null",
      "deletedAt":"null",
      "sessionKey":"80900064-0c68-45b5-988b-82df5ba55419",
      "expiration":"2019-01-07T14:29:19.549-08:00"
   }
}
```

## logout

This API is called in order to begin a session for a user account

#### Route

/user_accounts/logout

#### Method

PATCH

#### Parameters

1. user
   * The user making the API call
   * Datatype: UserAccount
1. request
   * The incoming request
   * Datatype: Request
   
#### Sample Response

```json
{
   "createdAt":"2019-01-06T14:29:19.561-08:00",
   "updatedAt":"null",
   "deletedAt":"null",
   "userMetadata":{
      "createdAt":"2019-01-06T14:29:19.556-08:00",
      "updatedAt":"null",
      "deletedAt":"null",
      "email":"dev0@ncnt.io",
      "firstname":"dev0",
      "lastname":"ncnt0",
      "metadatas":[

      ]
   },
   "cryptoKeyPair":{
      "createdAt":"2019-01-06T14:29:19.558-08:00",
      "updatedAt":"null",
      "deletedAt":"null",
      "publicKey":"[B@3ce7cd16"
   },
   "apiCreds":{
      "createdAt":"2019-01-06T14:29:19.559-08:00",
      "updatedAt":"null",
      "deletedAt":"null",
      "apiKey":"[B@2b72c09b"
   },
   "session":{
      "createdAt":"2019-01-06T14:29:19.560-08:00",
      "updatedAt":"null",
      "deletedAt":"null",
      "sessionKey":"80900064-0c68-45b5-988b-82df5ba55419",
      "expiration":"2019-01-07T14:29:19.549-08:00"
   }
}
```

# Challenge APIs

The following APIs will interact with the Challenges controller.

## retrieveAllChallenges

## retrieveAllChallengesForUser

## retrieveChallenge

## modifyChallengeState

## shareChallenge

## endChallenge

## redeemChallenge

# ChallengeSetting APIs

The following APIs will interact with the ChallengeSettings controller.

## retrieveChallengeSettings

# CompletionCriteria APIs

The following APIs will interact with the CompletionCriterias controller

## retrieveCompletionCriteria

# Reward APIs

The following APIs will interact with the Rewards controller.

## retrieveReward

# RewardPool APIs

The following APIs will interact with the RewardPools controller.

## retrieveRewardPool

## addToRewardPool

# RewardType APIs

The following APIs will interact with the RewardTypes controller.

## retrieveRewardType

# Token APIs

The following APIs will interact with the Tokens controller.

## retrieveAllTokens

## retrieveToken

## sendTokens
   
# Transaction APIs

The following APIs will interact with the Transactions controller.

## retrieveAllTransactions

## retrieveAllTransactionsForUser

## retrieveAllTransactionsForChallenge

##retrieveProvenanceChainFIFO

## createTransaction