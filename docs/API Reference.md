Table of Contents

1. [User APIs](#user-apis)
   + [Find One User](#find-one-user)
   + [GET (requestData object must pass data in as query parameters instead of request body) User Balances](#GET (requestData object must pass data in as query parameters instead of request body)-user-balances)
   + [Create User](#user-create)
   + [Login](#login)
   + [Logout](#logout)
1. [Challenge APIs](#challenge-apis)
   + [Find One Challenge](#find-one-challenge)
   + [Find All Challenges](#find-all-challenges)
   + [GET (requestData object must pass data in as query parameters instead of request body) All Balances For a Challenge](#GET (requestData object must pass data in as query parameters instead of request body)-all-balances-for-a-challenge)
   + [Create Challenge](#create-challenge)
   + [Share Challenge](#share-challenge)
   + [Redeem Challenge](#redeem-challenge)
   + [Complete Challenge](#complete-challenge)
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
   
# User Authentication

Most API calls will require user authentication. All that is required for this is the presence of an "Authorization" header
 in the request with a value that has the following format: "Basic {apiKey:secretKey}". This follows standard RESTful API best 
 practices, which are outlined in this [article](https://blog.restcase.com/restful-api-authentication-basics/).
   
# User APIs

The following APIs will interact with the UserAccountsController.

## Find One User

This API is a simple retrieval for a single user account

#### Route

/user

#### Method

GET (requestData object must pass data in as query parameters instead of request body)

#### Parameters

1. user
   * The user making the API call
   * Datatype: UserAccount
1. requestData
   * Datatype: Object
   * Contains the following fields:
      * secretKey - the caller's API secret key for verification
1. id
   * The ID of the user to be retrieved
   * Datatype: Int
   
#### Sample Response
```json
{
  "statusCode" : 200,
  "body" : {
    "createdAt" : "2019-01-23T12:24:40.428-08:00",
    "updatedAt" : "null",
    "deletedAt" : "null",
    "userMetadata" : {
      "createdAt" : "2019-01-23T12:24:40.424-08:00",
      "updatedAt" : "null",
      "deletedAt" : "null",
      "email" : "dev0@ncnt.io",
      "firstname" : "dev0",
      "lastname" : "ncnt0",
      "metadatas" : [ {
        "createdAt" : "2019-01-23T12:24:40.439-08:00",
        "updatedAt" : "null",
        "deletedAt" : "null",
        "key" : "test1key",
        "value" : "test1val"
      } ]
    },
    "cryptoKeyPair" : {
      "createdAt" : "2019-01-23T12:24:40.425-08:00",
      "updatedAt" : "null",
      "deletedAt" : "null",
      "publicKey" : "[B@286b2fe0"
    },
    "apiCreds" : {
      "createdAt" : "2019-01-23T12:24:40.426-08:00",
      "updatedAt" : "null",
      "deletedAt" : "null",
      "apiKey" : "[B@de6e913"
    },
    "session" : {
      "createdAt" : "2019-01-23T12:24:40.427-08:00",
      "updatedAt" : "null",
      "deletedAt" : "null",
      "sessionKey" : "1ac8e722-480f-4125-9b28-550828ea091c",
      "expiration" : "2019-01-24T12:24:40.418-08:00"
    }
  },
  "headers" : {
    "X-Powered-By" : "AWS Lambda & Serverless"
  },
  "base64Encoded" : false
}
```

## GET (requestData object must pass data in as query parameters instead of request body) User Balances

This API will return all of the caller's balances for each challenge that they have participated in

#### Route

/user/balances

#### Method

GET (requestData object must pass data in as query parameters instead of request body)

#### Parameters

1. user
   * The user making the API call
   * Datatype: UserAccount
1. requestData
   * Datatype: Object
   * Contains the following fields:
      * secretKey - the caller's API secret key for verification
   
#### Sample Response

```json
{
  "statusCode" : 200,
  "body" : {
    "challenGET (requestData object must pass data in as query parameters instead of request body)oUnsharedTransactions" : [ {
      "challenge" : {
        "createdAt" : "2019-01-23T13:55:04.401-08:00",
        "updatedAt" : "null",
        "deletedAt" : "null",
        "parentChallenge" : "null",
        "challengeSettings" : {
          "createdAt" : "2019-01-23T13:55:04.375-08:00",
          "updatedAt" : "null",
          "deletedAt" : "null",
          "name" : "TESTname1",
          "description" : "TESTdescription1",
          "imageUrl" : "TESTimageUrl1",
          "sponsorName" : "TESTsponsorName1",
          "expiration" : "2019-01-24T13:55:04.182-08:00",
          "shareExpiration" : "2019-01-24T13:55:04.182-08:00",
          "admin" : 1,
          "offChain" : false,
          "maxShares" : 100,
          "maxRewards" : "null",
          "maxDistributionFeeReward" : "null",
          "maxSharesPerReceivedShare" : "null",
          "maxDepth" : "null",
          "maxNodes" : "null"
        },
        "subChallenges" : [ ],
        "completionCriteria" : {
          "createdAt" : "2019-01-23T13:55:04.397-08:00",
          "updatedAt" : "null",
          "deletedAt" : "null",
          "address" : "[B@41c6bd06",
          "reward" : {
            "createdAt" : "2019-01-23T13:55:04.392-08:00",
            "updatedAt" : "null",
            "deletedAt" : "null",
            "type" : {
              "createdAt" : "2019-01-23T13:55:04.256-08:00",
              "updatedAt" : "null",
              "deletedAt" : "null",
              "audience" : "PROVIDENCE",
              "type" : "EVEN"
            },
            "pool" : {
              "createdAt" : "2019-01-23T13:55:04.390-08:00",
              "updatedAt" : "null",
              "deletedAt" : "null",
              "cryptoKeyPair" : {
                "createdAt" : "2019-01-23T13:55:04.387-08:00",
                "updatedAt" : "null",
                "deletedAt" : "null",
                "publicKey" : "[B@737e03e8"
              },
              "transactions" : [ ]
            },
            "metadatas" : [ ]
          },
          "prereq" : [ ]
        },
        "cryptoKeyPair" : {
          "createdAt" : "2019-01-23T13:55:04.369-08:00",
          "updatedAt" : "null",
          "deletedAt" : "null",
          "publicKey" : "[B@60557e44"
        },
        "distributionFeeReward" : {
          "createdAt" : "2019-01-23T13:55:04.378-08:00",
          "updatedAt" : "null",
          "deletedAt" : "null",
          "type" : {
            "createdAt" : "2019-01-23T13:55:04.221-08:00",
            "updatedAt" : "null",
            "deletedAt" : "null",
            "audience" : "PROVIDENCE",
            "type" : "SINGLE"
          },
          "pool" : {
            "createdAt" : "2019-01-23T13:55:04.374-08:00",
            "updatedAt" : "null",
            "deletedAt" : "null",
            "cryptoKeyPair" : {
              "createdAt" : "2019-01-23T13:55:04.369-08:00",
              "updatedAt" : "null",
              "deletedAt" : "null",
              "publicKey" : "[B@3ee405d5"
            },
            "transactions" : [ ]
          },
          "metadatas" : [ ]
        }
      },
      "shareTransactionList" : {
        "transactionsToShares" : [ {
          "transaction" : {
            "createdAt" : "2019-01-23T13:55:04.414-08:00",
            "updatedAt" : "2019-01-23T13:55:04.508-08:00",
            "deletedAt" : "null",
            "from" : "[B@60557e44",
            "to" : "[B@41c6bd06",
            "action" : {
              "createdAt" : "2019-01-23T13:55:04.411-08:00",
              "updatedAt" : "null",
              "deletedAt" : "null",
              "type" : "SHARE",
              "data" : 2,
              "dataType" : "Challenge"
            },
            "previousTransactionId" : "null",
            "metadatas" : [ {
              "createdAt" : "2019-01-23T13:55:04.412-08:00",
              "updatedAt" : "2019-01-23T13:55:04.508-08:00",
              "deletedAt" : "null",
              "key" : "challengeId",
              "value" : "2"
            }, {
              "createdAt" : "2019-01-23T13:55:04.412-08:00",
              "updatedAt" : "2019-01-23T13:55:04.508-08:00",
              "deletedAt" : "null",
              "key" : "offChain",
              "value" : "false"
            }, {
              "createdAt" : "2019-01-23T13:55:04.412-08:00",
              "updatedAt" : "2019-01-23T13:55:04.508-08:00",
              "deletedAt" : "null",
              "key" : "shareExpiration",
              "value" : "2019-01-24T21:55:04.182Z"
            }, {
              "createdAt" : "2019-01-23T13:55:04.412-08:00",
              "updatedAt" : "2019-01-23T13:55:04.508-08:00",
              "deletedAt" : "null",
              "key" : "maxShares",
              "value" : "100"
            } ]
          },
          "shares" : 100
        } ]
      }
    }, {
      "challenge" : {
        "createdAt" : "2019-01-23T13:55:04.487-08:00",
        "updatedAt" : "2019-01-23T13:55:04.508-08:00",
        "deletedAt" : "null",
        "parentChallenge" : "1",
        "challengeSettings" : {
          "createdAt" : "2019-01-23T13:55:04.473-08:00",
          "updatedAt" : "null",
          "deletedAt" : "null",
          "name" : "TESTname0",
          "description" : "TESTdescription0",
          "imageUrl" : "TESTimageUrl0",
          "sponsorName" : "TESTsponsorName0",
          "expiration" : "2019-01-24T13:55:04.459-08:00",
          "shareExpiration" : "2019-01-24T13:55:04.459-08:00",
          "admin" : 1,
          "offChain" : false,
          "maxShares" : 100,
          "maxRewards" : "null",
          "maxDistributionFeeReward" : "null",
          "maxSharesPerReceivedShare" : "null",
          "maxDepth" : "null",
          "maxNodes" : "null"
        },
        "subChallenges" : [ {
          "createdAt" : "2019-01-23T13:55:04.488-08:00",
          "updatedAt" : "2019-01-23T13:55:04.508-08:00",
          "deletedAt" : "null",
          "subChallengeId" : 1,
          "type" : "SYNC"
        }, {
          "createdAt" : "2019-01-23T13:55:04.488-08:00",
          "updatedAt" : "2019-01-23T13:55:04.508-08:00",
          "deletedAt" : "null",
          "subChallengeId" : 2,
          "type" : "ASYNC"
        } ],
        "completionCriteria" : {
          "createdAt" : "2019-01-23T13:55:04.485-08:00",
          "updatedAt" : "null",
          "deletedAt" : "null",
          "address" : "[B@41c6bd06",
          "reward" : {
            "createdAt" : "2019-01-23T13:55:04.483-08:00",
            "updatedAt" : "null",
            "deletedAt" : "null",
            "type" : {
              "createdAt" : "2019-01-23T13:55:04.256-08:00",
              "updatedAt" : "null",
              "deletedAt" : "null",
              "audience" : "PROVIDENCE",
              "type" : "EVEN"
            },
            "pool" : {
              "createdAt" : "2019-01-23T13:55:04.482-08:00",
              "updatedAt" : "null",
              "deletedAt" : "null",
              "cryptoKeyPair" : {
                "createdAt" : "2019-01-23T13:55:04.482-08:00",
                "updatedAt" : "null",
                "deletedAt" : "null",
                "publicKey" : "[B@7d55a4cf"
              },
              "transactions" : [ ]
            },
            "metadatas" : [ ]
          },
          "prereq" : [ ]
        },
        "cryptoKeyPair" : {
          "createdAt" : "2019-01-23T13:55:04.471-08:00",
          "updatedAt" : "null",
          "deletedAt" : "null",
          "publicKey" : "[B@6da76a4a"
        },
        "distributionFeeReward" : {
          "createdAt" : "2019-01-23T13:55:04.474-08:00",
          "updatedAt" : "null",
          "deletedAt" : "null",
          "type" : {
            "createdAt" : "2019-01-23T13:55:04.221-08:00",
            "updatedAt" : "null",
            "deletedAt" : "null",
            "audience" : "PROVIDENCE",
            "type" : "SINGLE"
          },
          "pool" : {
            "createdAt" : "2019-01-23T13:55:04.472-08:00",
            "updatedAt" : "null",
            "deletedAt" : "null",
            "cryptoKeyPair" : {
              "createdAt" : "2019-01-23T13:55:04.471-08:00",
              "updatedAt" : "null",
              "deletedAt" : "null",
              "publicKey" : "[B@2e92ee03"
            },
            "transactions" : [ ]
          },
          "metadatas" : [ ]
        }
      },
      "shareTransactionList" : {
        "transactionsToShares" : [ {
          "transaction" : {
            "createdAt" : "2019-01-23T13:55:04.504-08:00",
            "updatedAt" : "2019-01-23T13:55:04.508-08:00",
            "deletedAt" : "null",
            "from" : "[B@6da76a4a",
            "to" : "[B@41c6bd06",
            "action" : {
              "createdAt" : "2019-01-23T13:55:04.502-08:00",
              "updatedAt" : "null",
              "deletedAt" : "null",
              "type" : "SHARE",
              "data" : 4,
              "dataType" : "Challenge"
            },
            "previousTransactionId" : "null",
            "metadatas" : [ {
              "createdAt" : "2019-01-23T13:55:04.503-08:00",
              "updatedAt" : "2019-01-23T13:55:04.508-08:00",
              "deletedAt" : "null",
              "key" : "challengeId",
              "value" : "4"
            }, {
              "createdAt" : "2019-01-23T13:55:04.503-08:00",
              "updatedAt" : "2019-01-23T13:55:04.508-08:00",
              "deletedAt" : "null",
              "key" : "offChain",
              "value" : "false"
            }, {
              "createdAt" : "2019-01-23T13:55:04.503-08:00",
              "updatedAt" : "2019-01-23T13:55:04.508-08:00",
              "deletedAt" : "null",
              "key" : "shareExpiration",
              "value" : "2019-01-24T21:55:04.459Z"
            }, {
              "createdAt" : "2019-01-23T13:55:04.503-08:00",
              "updatedAt" : "2019-01-23T13:55:04.508-08:00",
              "deletedAt" : "null",
              "key" : "maxShares",
              "value" : "100"
            } ]
          },
          "shares" : 100
        } ]
      }
    }, {
      "challenge" : {
        "createdAt" : "2019-01-23T13:55:04.276-08:00",
        "updatedAt" : "null",
        "deletedAt" : "null",
        "parentChallenge" : "null",
        "challengeSettings" : {
          "createdAt" : "2019-01-23T13:55:04.240-08:00",
          "updatedAt" : "null",
          "deletedAt" : "null",
          "name" : "TESTname0",
          "description" : "TESTdescription0",
          "imageUrl" : "TESTimageUrl0",
          "sponsorName" : "TESTsponsorName0",
          "expiration" : "2019-01-24T13:55:04.182-08:00",
          "shareExpiration" : "2019-01-24T13:55:04.182-08:00",
          "admin" : 1,
          "offChain" : false,
          "maxShares" : 100,
          "maxRewards" : "null",
          "maxDistributionFeeReward" : "null",
          "maxSharesPerReceivedShare" : "null",
          "maxDepth" : "null",
          "maxNodes" : "null"
        },
        "subChallenges" : [ ],
        "completionCriteria" : {
          "createdAt" : "2019-01-23T13:55:04.267-08:00",
          "updatedAt" : "null",
          "deletedAt" : "null",
          "address" : "[B@41c6bd06",
          "reward" : {
            "createdAt" : "2019-01-23T13:55:04.266-08:00",
            "updatedAt" : "null",
            "deletedAt" : "null",
            "type" : {
              "createdAt" : "2019-01-23T13:55:04.256-08:00",
              "updatedAt" : "null",
              "deletedAt" : "null",
              "audience" : "PROVIDENCE",
              "type" : "EVEN"
            },
            "pool" : {
              "createdAt" : "2019-01-23T13:55:04.264-08:00",
              "updatedAt" : "null",
              "deletedAt" : "null",
              "cryptoKeyPair" : {
                "createdAt" : "2019-01-23T13:55:04.262-08:00",
                "updatedAt" : "null",
                "deletedAt" : "null",
                "publicKey" : "[B@156e5092"
              },
              "transactions" : [ ]
            },
            "metadatas" : [ ]
          },
          "prereq" : [ ]
        },
        "cryptoKeyPair" : {
          "createdAt" : "2019-01-23T13:55:04.233-08:00",
          "updatedAt" : "null",
          "deletedAt" : "null",
          "publicKey" : "[B@4cf0e137"
        },
        "distributionFeeReward" : {
          "createdAt" : "2019-01-23T13:55:04.244-08:00",
          "updatedAt" : "null",
          "deletedAt" : "null",
          "type" : {
            "createdAt" : "2019-01-23T13:55:04.221-08:00",
            "updatedAt" : "null",
            "deletedAt" : "null",
            "audience" : "PROVIDENCE",
            "type" : "SINGLE"
          },
          "pool" : {
            "createdAt" : "2019-01-23T13:55:04.236-08:00",
            "updatedAt" : "null",
            "deletedAt" : "null",
            "cryptoKeyPair" : {
              "createdAt" : "2019-01-23T13:55:04.233-08:00",
              "updatedAt" : "null",
              "deletedAt" : "null",
              "publicKey" : "[B@60bf6fcc"
            },
            "transactions" : [ ]
          },
          "metadatas" : [ ]
        }
      },
      "shareTransactionList" : {
        "transactionsToShares" : [ {
          "transaction" : {
            "createdAt" : "2019-01-23T13:55:04.310-08:00",
            "updatedAt" : "2019-01-23T13:55:04.508-08:00",
            "deletedAt" : "null",
            "from" : "[B@4cf0e137",
            "to" : "[B@41c6bd06",
            "action" : {
              "createdAt" : "2019-01-23T13:55:04.290-08:00",
              "updatedAt" : "null",
              "deletedAt" : "null",
              "type" : "SHARE",
              "data" : 1,
              "dataType" : "Challenge"
            },
            "previousTransactionId" : "null",
            "metadatas" : [ {
              "createdAt" : "2019-01-23T13:55:04.298-08:00",
              "updatedAt" : "2019-01-23T13:55:04.508-08:00",
              "deletedAt" : "null",
              "key" : "challengeId",
              "value" : "1"
            }, {
              "createdAt" : "2019-01-23T13:55:04.298-08:00",
              "updatedAt" : "2019-01-23T13:55:04.508-08:00",
              "deletedAt" : "null",
              "key" : "offChain",
              "value" : "false"
            }, {
              "createdAt" : "2019-01-23T13:55:04.298-08:00",
              "updatedAt" : "2019-01-23T13:55:04.508-08:00",
              "deletedAt" : "null",
              "key" : "shareExpiration",
              "value" : "2019-01-24T21:55:04.182Z"
            }, {
              "createdAt" : "2019-01-23T13:55:04.299-08:00",
              "updatedAt" : "2019-01-23T13:55:04.508-08:00",
              "deletedAt" : "null",
              "key" : "maxShares",
              "value" : "100"
            } ]
          },
          "shares" : 100
        } ]
      }
    }, {
      "challenge" : {
        "createdAt" : "2019-01-23T13:55:04.448-08:00",
        "updatedAt" : "null",
        "deletedAt" : "null",
        "parentChallenge" : "null",
        "challengeSettings" : {
          "createdAt" : "2019-01-23T13:55:04.434-08:00",
          "updatedAt" : "null",
          "deletedAt" : "null",
          "name" : "TESTname2",
          "description" : "TESTdescription2",
          "imageUrl" : "TESTimageUrl2",
          "sponsorName" : "TESTsponsorName2",
          "expiration" : "2019-01-24T13:55:04.182-08:00",
          "shareExpiration" : "2019-01-24T13:55:04.182-08:00",
          "admin" : 1,
          "offChain" : false,
          "maxShares" : 100,
          "maxRewards" : "null",
          "maxDistributionFeeReward" : "null",
          "maxSharesPerReceivedShare" : "null",
          "maxDepth" : "null",
          "maxNodes" : "null"
        },
        "subChallenges" : [ ],
        "completionCriteria" : {
          "createdAt" : "2019-01-23T13:55:04.447-08:00",
          "updatedAt" : "null",
          "deletedAt" : "null",
          "address" : "[B@41c6bd06",
          "reward" : {
            "createdAt" : "2019-01-23T13:55:04.447-08:00",
            "updatedAt" : "null",
            "deletedAt" : "null",
            "type" : {
              "createdAt" : "2019-01-23T13:55:04.256-08:00",
              "updatedAt" : "null",
              "deletedAt" : "null",
              "audience" : "PROVIDENCE",
              "type" : "EVEN"
            },
            "pool" : {
              "createdAt" : "2019-01-23T13:55:04.446-08:00",
              "updatedAt" : "null",
              "deletedAt" : "null",
              "cryptoKeyPair" : {
                "createdAt" : "2019-01-23T13:55:04.445-08:00",
                "updatedAt" : "null",
                "deletedAt" : "null",
                "publicKey" : "[B@6a8d088e"
              },
              "transactions" : [ ]
            },
            "metadatas" : [ ]
          },
          "prereq" : [ ]
        },
        "cryptoKeyPair" : {
          "createdAt" : "2019-01-23T13:55:04.432-08:00",
          "updatedAt" : "null",
          "deletedAt" : "null",
          "publicKey" : "[B@6cf76041"
        },
        "distributionFeeReward" : {
          "createdAt" : "2019-01-23T13:55:04.436-08:00",
          "updatedAt" : "null",
          "deletedAt" : "null",
          "type" : {
            "createdAt" : "2019-01-23T13:55:04.221-08:00",
            "updatedAt" : "null",
            "deletedAt" : "null",
            "audience" : "PROVIDENCE",
            "type" : "SINGLE"
          },
          "pool" : {
            "createdAt" : "2019-01-23T13:55:04.433-08:00",
            "updatedAt" : "null",
            "deletedAt" : "null",
            "cryptoKeyPair" : {
              "createdAt" : "2019-01-23T13:55:04.432-08:00",
              "updatedAt" : "null",
              "deletedAt" : "null",
              "publicKey" : "[B@7ec09bf5"
            },
            "transactions" : [ ]
          },
          "metadatas" : [ ]
        }
      },
      "shareTransactionList" : {
        "transactionsToShares" : [ {
          "transaction" : {
            "createdAt" : "2019-01-23T13:55:04.456-08:00",
            "updatedAt" : "2019-01-23T13:55:04.508-08:00",
            "deletedAt" : "null",
            "from" : "[B@6cf76041",
            "to" : "[B@41c6bd06",
            "action" : {
              "createdAt" : "2019-01-23T13:55:04.452-08:00",
              "updatedAt" : "null",
              "deletedAt" : "null",
              "type" : "SHARE",
              "data" : 3,
              "dataType" : "Challenge"
            },
            "previousTransactionId" : "null",
            "metadatas" : [ {
              "createdAt" : "2019-01-23T13:55:04.454-08:00",
              "updatedAt" : "2019-01-23T13:55:04.508-08:00",
              "deletedAt" : "null",
              "key" : "challengeId",
              "value" : "3"
            }, {
              "createdAt" : "2019-01-23T13:55:04.454-08:00",
              "updatedAt" : "2019-01-23T13:55:04.508-08:00",
              "deletedAt" : "null",
              "key" : "offChain",
              "value" : "false"
            }, {
              "createdAt" : "2019-01-23T13:55:04.454-08:00",
              "updatedAt" : "2019-01-23T13:55:04.508-08:00",
              "deletedAt" : "null",
              "key" : "shareExpiration",
              "value" : "2019-01-24T21:55:04.182Z"
            }, {
              "createdAt" : "2019-01-23T13:55:04.454-08:00",
              "updatedAt" : "2019-01-23T13:55:04.508-08:00",
              "deletedAt" : "null",
              "key" : "maxShares",
              "value" : "100"
            } ]
          },
          "shares" : 100
        } ]
      }
    } ]
  },
  "headers" : {
    "X-Powered-By" : "AWS Lambda & Serverless"
  },
  "base64Encoded" : false
}
```

## Create User

This API is used for the creation of a new User instance

#### Route

/user

#### Method

POST 

#### Parameters 

1. user
   * The user making the API call
   * Datatype: UserAccount
1. requestData
   * Datatype: Object
   * Contains the following fields:
      * secretKey - the caller's API secret key for verification
      * body - a nested object containing the following user data fields (as Strings)
         * email 
         * firstname
         * lastname

#### Sample Response
```json
{
  "statusCode" : 200,
  "body" : {
    "value" : {
      "createdAt" : "2019-01-24T20:36:47.373Z",
      "updatedAt" : "null",
      "deletedAt" : "null",
      "userMetadata" : {
        "createdAt" : "2019-01-24T20:36:47.372Z",
        "updatedAt" : "null",
        "deletedAt" : "null",
        "email" : "dev@ncnt.io",
        "firstname" : "dev",
        "lastname" : "ncnt",
        "metadatas" : [ ]
      },
      "cryptoKeyPair" : {
        "createdAt" : "2019-01-24T20:36:47.372Z",
        "updatedAt" : "null",
        "deletedAt" : "null",
        "publicKey" : "[B@6d6e2113"
      },
      "apiCreds" : {
        "createdAt" : "2019-01-24T20:36:47.372Z",
        "updatedAt" : "null",
        "deletedAt" : "null",
        "apiKey" : "[B@1673c486"
      },
      "session" : {
        "createdAt" : "2019-01-24T20:36:47.372Z",
        "updatedAt" : "null",
        "deletedAt" : "null",
        "sessionKey" : "dc62fd6b-4867-4247-a18e-9a724f2f1010",
        "expiration" : "2019-01-25T20:36:47.368Z"
      }
    },
    "privateKey" : "[C@6edeb1aa",
    "secretKey" : "[C@3c06f5c3"
  },
  "headers" : {
    "X-Powered-By" : "AWS Lambda & Serverless"
  },
  "base64Encoded" : false
}
```

## Login

This API is called in order to begin a session for a user account

#### Route

/user/login

#### Method

PATCH

#### Parameters

1. user
   * The user making the API call
   * Datatype: UserAccount
1. requestData
   * Datatype: Object
   * Contains the following fields:
      * secretKey - the caller's API secret key for verification
   
#### Sample Response

```json
{
  "statusCode" : 200,
  "body" : {
    "createdAt" : "2019-01-24T12:36:47.486-08:00",
    "updatedAt" : "null",
    "deletedAt" : "null",
    "userMetadata" : {
      "createdAt" : "2019-01-24T12:36:47.484-08:00",
      "updatedAt" : "null",
      "deletedAt" : "null",
      "email" : "dev0@ncnt.io",
      "firstname" : "dev0",
      "lastname" : "ncnt0",
      "metadatas" : [ ]
    },
    "cryptoKeyPair" : {
      "createdAt" : "2019-01-24T12:36:47.485-08:00",
      "updatedAt" : "null",
      "deletedAt" : "null",
      "publicKey" : "[B@455851a"
    },
    "apiCreds" : {
      "createdAt" : "2019-01-24T12:36:47.485-08:00",
      "updatedAt" : "null",
      "deletedAt" : "null",
      "apiKey" : "[B@624f330b"
    },
    "session" : {
      "createdAt" : "2019-01-24T12:36:47.485-08:00",
      "updatedAt" : "null",
      "deletedAt" : "null",
      "sessionKey" : "05bcb7e0-512c-4c7e-a937-ee86819846e1",
      "expiration" : "2019-01-25T12:36:47.479-08:00"
    }
  },
  "headers" : {
    "X-Powered-By" : "AWS Lambda & Serverless"
  },
  "base64Encoded" : false
}
```

## Logout

This API is called in order to begin a session for a user account

#### Route

/user/logout

#### Method

PATCH

#### Parameters

1. user
   * The user making the API call
   * Datatype: UserAccount
1. requestData
   * Datatype: Object
   * Contains the following fields:
      * secretKey - the caller's API secret key for verification
   
#### Sample Response

```json
{
  "statusCode" : 200,
  "body" : {
    "createdAt" : "2019-01-24T12:36:47.573-08:00",
    "updatedAt" : "null",
    "deletedAt" : "null",
    "userMetadata" : {
      "createdAt" : "2019-01-24T12:36:47.571-08:00",
      "updatedAt" : "null",
      "deletedAt" : "null",
      "email" : "dev0@ncnt.io",
      "firstname" : "dev0",
      "lastname" : "ncnt0",
      "metadatas" : [ ]
    },
    "cryptoKeyPair" : {
      "createdAt" : "2019-01-24T12:36:47.572-08:00",
      "updatedAt" : "null",
      "deletedAt" : "null",
      "publicKey" : "[B@661b77ad"
    },
    "apiCreds" : {
      "createdAt" : "2019-01-24T12:36:47.573-08:00",
      "updatedAt" : "null",
      "deletedAt" : "null",
      "apiKey" : "[B@510ab846"
    },
    "session" : {
      "createdAt" : "2019-01-24T12:36:47.573-08:00",
      "updatedAt" : "null",
      "deletedAt" : "null",
      "sessionKey" : "5d4bfbf3-7c90-4885-8110-fc9a0bc49e21",
      "expiration" : "2019-01-24T20:36:47.581Z"
    }
  },
  "headers" : {
    "X-Powered-By" : "AWS Lambda & Serverless"
  },
  "base64Encoded" : false
}
```

# Challenge APIs

The following APIs will interact with the Challenges controller.

## Find One Challenge

This API is called in order to retrieve the data for a single challenge

#### Route

/challenge

#### Method

GET (requestData object must pass data in as query parameters instead of request body)

#### Parameters

1. user
   * The user making the API call
   * Datatype: UserAccount
1. requestData
   * Datatype: Object
   * Contains the following fields:
      * secretKey - the caller's API secret key for verification
1. id
   * The ID of the challenge to be retrieved
   * Datatype: Int
   
#### Sample Response

```json
{
  "statusCode" : 200,
  "body" : {
    "createdAt" : "2019-01-24T12:36:40.636-08:00",
    "updatedAt" : "null",
    "deletedAt" : "null",
    "parentChallenge" : "null",
    "challengeSettings" : {
      "createdAt" : "2019-01-24T12:36:40.630-08:00",
      "updatedAt" : "null",
      "deletedAt" : "null",
      "name" : "TESTname0",
      "description" : "TESTdescription0",
      "imageUrl" : "TESTimageUrl0",
      "sponsorName" : "TESTsponsorName0",
      "expiration" : "2019-01-25T12:36:40.623-08:00",
      "shareExpiration" : "2019-01-25T12:36:40.623-08:00",
      "admin" : 1,
      "offChain" : false,
      "maxShares" : 100,
      "maxRewards" : "null",
      "maxDistributionFeeReward" : "null",
      "maxSharesPerReceivedShare" : "null",
      "maxDepth" : "null",
      "maxNodes" : "null"
    },
    "subChallenges" : [ ],
    "completionCriteria" : {
      "createdAt" : "2019-01-24T12:36:40.635-08:00",
      "updatedAt" : "null",
      "deletedAt" : "null",
      "address" : "[B@5320f6f0",
      "reward" : {
        "createdAt" : "2019-01-24T12:36:40.635-08:00",
        "updatedAt" : "null",
        "deletedAt" : "null",
        "type" : {
          "createdAt" : "2019-01-24T12:36:40.635-08:00",
          "updatedAt" : "null",
          "deletedAt" : "null",
          "audience" : "PROVIDENCE",
          "type" : "EVEN"
        },
        "pool" : {
          "createdAt" : "2019-01-24T12:36:40.635-08:00",
          "updatedAt" : "null",
          "deletedAt" : "null",
          "cryptoKeyPair" : {
            "createdAt" : "2019-01-24T12:36:40.635-08:00",
            "updatedAt" : "null",
            "deletedAt" : "null",
            "publicKey" : "[B@5ed940c1"
          },
          "transactions" : [ ]
        },
        "metadatas" : [ ]
      },
      "prereq" : [ ]
    },
    "cryptoKeyPair" : {
      "createdAt" : "2019-01-24T12:36:40.630-08:00",
      "updatedAt" : "null",
      "deletedAt" : "null",
      "publicKey" : "[B@119733e9"
    },
    "distributionFeeReward" : {
      "createdAt" : "2019-01-24T12:36:40.631-08:00",
      "updatedAt" : "null",
      "deletedAt" : "null",
      "type" : {
        "createdAt" : "2019-01-24T12:36:40.630-08:00",
        "updatedAt" : "null",
        "deletedAt" : "null",
        "audience" : "PROVIDENCE",
        "type" : "SINGLE"
      },
      "pool" : {
        "createdAt" : "2019-01-24T12:36:40.630-08:00",
        "updatedAt" : "null",
        "deletedAt" : "null",
        "cryptoKeyPair" : {
          "createdAt" : "2019-01-24T12:36:40.630-08:00",
          "updatedAt" : "null",
          "deletedAt" : "null",
          "publicKey" : "[B@15185f88"
        },
        "transactions" : [ ]
      },
      "metadatas" : [ ]
    }
  },
  "headers" : {
    "X-Powered-By" : "AWS Lambda & Serverless"
  },
  "base64Encoded" : false
}
```

## Find All Challenges

This API is called in order to retrieve the data for all challenges for the caller

#### Route

/challenge

#### Method

GET (requestData object must pass data in as query parameters instead of request body)

#### Parameters

1. user
   * The user making the API call
   * Datatype: UserAccount
1. requestData
   * Datatype: Object
   * Contains the following fields:
      * secretKey - the caller's API secret key for verification
   
#### Sample Response

```json
{
  "statusCode" : 200,
  "body" : [ {
    "createdAt" : "2019-01-24T12:36:40.088-08:00",
    "updatedAt" : "null",
    "deletedAt" : "null",
    "parentChallenge" : "null",
    "challengeSettings" : {
      "createdAt" : "2019-01-24T12:36:40.083-08:00",
      "updatedAt" : "null",
      "deletedAt" : "null",
      "name" : "TESTname0",
      "description" : "TESTdescription0",
      "imageUrl" : "TESTimageUrl0",
      "sponsorName" : "TESTsponsorName0",
      "expiration" : "2019-01-25T12:36:40.075-08:00",
      "shareExpiration" : "2019-01-25T12:36:40.075-08:00",
      "admin" : 1,
      "offChain" : false,
      "maxShares" : 100,
      "maxRewards" : "null",
      "maxDistributionFeeReward" : "null",
      "maxSharesPerReceivedShare" : "null",
      "maxDepth" : "null",
      "maxNodes" : "null"
    },
    "subChallenges" : [ ],
    "completionCriteria" : {
      "createdAt" : "2019-01-24T12:36:40.088-08:00",
      "updatedAt" : "null",
      "deletedAt" : "null",
      "address" : "[B@7af25dd1",
      "reward" : {
        "createdAt" : "2019-01-24T12:36:40.088-08:00",
        "updatedAt" : "null",
        "deletedAt" : "null",
        "type" : {
          "createdAt" : "2019-01-24T12:36:40.087-08:00",
          "updatedAt" : "null",
          "deletedAt" : "null",
          "audience" : "PROVIDENCE",
          "type" : "EVEN"
        },
        "pool" : {
          "createdAt" : "2019-01-24T12:36:40.088-08:00",
          "updatedAt" : "null",
          "deletedAt" : "null",
          "cryptoKeyPair" : {
            "createdAt" : "2019-01-24T12:36:40.088-08:00",
            "updatedAt" : "null",
            "deletedAt" : "null",
            "publicKey" : "[B@4b60eeb3"
          },
          "transactions" : [ ]
        },
        "metadatas" : [ ]
      },
      "prereq" : [ ]
    },
    "cryptoKeyPair" : {
      "createdAt" : "2019-01-24T12:36:40.082-08:00",
      "updatedAt" : "null",
      "deletedAt" : "null",
      "publicKey" : "[B@2d4292b7"
    },
    "distributionFeeReward" : {
      "createdAt" : "2019-01-24T12:36:40.083-08:00",
      "updatedAt" : "null",
      "deletedAt" : "null",
      "type" : {
        "createdAt" : "2019-01-24T12:36:40.082-08:00",
        "updatedAt" : "null",
        "deletedAt" : "null",
        "audience" : "PROVIDENCE",
        "type" : "SINGLE"
      },
      "pool" : {
        "createdAt" : "2019-01-24T12:36:40.083-08:00",
        "updatedAt" : "null",
        "deletedAt" : "null",
        "cryptoKeyPair" : {
          "createdAt" : "2019-01-24T12:36:40.082-08:00",
          "updatedAt" : "null",
          "deletedAt" : "null",
          "publicKey" : "[B@1fc13c0e"
        },
        "transactions" : [ ]
      },
      "metadatas" : [ ]
    }
  }, {
    "createdAt" : "2019-01-24T12:36:40.118-08:00",
    "updatedAt" : "null",
    "deletedAt" : "null",
    "parentChallenge" : "null",
    "challengeSettings" : {
      "createdAt" : "2019-01-24T12:36:40.113-08:00",
      "updatedAt" : "null",
      "deletedAt" : "null",
      "name" : "TESTname2",
      "description" : "TESTdescription2",
      "imageUrl" : "TESTimageUrl2",
      "sponsorName" : "TESTsponsorName2",
      "expiration" : "2019-01-25T12:36:40.075-08:00",
      "shareExpiration" : "2019-01-25T12:36:40.075-08:00",
      "admin" : 1,
      "offChain" : false,
      "maxShares" : 100,
      "maxRewards" : "null",
      "maxDistributionFeeReward" : "null",
      "maxSharesPerReceivedShare" : "null",
      "maxDepth" : "null",
      "maxNodes" : "null"
    },
    "subChallenges" : [ ],
    "completionCriteria" : {
      "createdAt" : "2019-01-24T12:36:40.118-08:00",
      "updatedAt" : "null",
      "deletedAt" : "null",
      "address" : "[B@7af25dd1",
      "reward" : {
        "createdAt" : "2019-01-24T12:36:40.118-08:00",
        "updatedAt" : "null",
        "deletedAt" : "null",
        "type" : {
          "createdAt" : "2019-01-24T12:36:40.087-08:00",
          "updatedAt" : "null",
          "deletedAt" : "null",
          "audience" : "PROVIDENCE",
          "type" : "EVEN"
        },
        "pool" : {
          "createdAt" : "2019-01-24T12:36:40.118-08:00",
          "updatedAt" : "null",
          "deletedAt" : "null",
          "cryptoKeyPair" : {
            "createdAt" : "2019-01-24T12:36:40.117-08:00",
            "updatedAt" : "null",
            "deletedAt" : "null",
            "publicKey" : "[B@1891e690"
          },
          "transactions" : [ ]
        },
        "metadatas" : [ ]
      },
      "prereq" : [ ]
    },
    "cryptoKeyPair" : {
      "createdAt" : "2019-01-24T12:36:40.112-08:00",
      "updatedAt" : "null",
      "deletedAt" : "null",
      "publicKey" : "[B@5b98d389"
    },
    "distributionFeeReward" : {
      "createdAt" : "2019-01-24T12:36:40.113-08:00",
      "updatedAt" : "null",
      "deletedAt" : "null",
      "type" : {
        "createdAt" : "2019-01-24T12:36:40.082-08:00",
        "updatedAt" : "null",
        "deletedAt" : "null",
        "audience" : "PROVIDENCE",
        "type" : "SINGLE"
      },
      "pool" : {
        "createdAt" : "2019-01-24T12:36:40.113-08:00",
        "updatedAt" : "null",
        "deletedAt" : "null",
        "cryptoKeyPair" : {
          "createdAt" : "2019-01-24T12:36:40.112-08:00",
          "updatedAt" : "null",
          "deletedAt" : "null",
          "publicKey" : "[B@2ba5529a"
        },
        "transactions" : [ ]
      },
      "metadatas" : [ ]
    }
  }, {
    "createdAt" : "2019-01-24T12:36:40.134-08:00",
    "updatedAt" : "2019-01-24T12:36:40.137-08:00",
    "deletedAt" : "null",
    "parentChallenge" : "1",
    "challengeSettings" : {
      "createdAt" : "2019-01-24T12:36:40.128-08:00",
      "updatedAt" : "null",
      "deletedAt" : "null",
      "name" : "TESTname0",
      "description" : "TESTdescription0",
      "imageUrl" : "TESTimageUrl0",
      "sponsorName" : "TESTsponsorName0",
      "expiration" : "2019-01-25T12:36:40.120-08:00",
      "shareExpiration" : "2019-01-25T12:36:40.120-08:00",
      "admin" : 1,
      "offChain" : false,
      "maxShares" : 100,
      "maxRewards" : "null",
      "maxDistributionFeeReward" : "null",
      "maxSharesPerReceivedShare" : "null",
      "maxDepth" : "null",
      "maxNodes" : "null"
    },
    "subChallenges" : [ {
      "createdAt" : "2019-01-24T12:36:40.134-08:00",
      "updatedAt" : "2019-01-24T12:36:40.137-08:00",
      "deletedAt" : "null",
      "subChallengeId" : 1,
      "type" : "SYNC"
    }, {
      "createdAt" : "2019-01-24T12:36:40.134-08:00",
      "updatedAt" : "2019-01-24T12:36:40.137-08:00",
      "deletedAt" : "null",
      "subChallengeId" : 2,
      "type" : "ASYNC"
    } ],
    "completionCriteria" : {
      "createdAt" : "2019-01-24T12:36:40.134-08:00",
      "updatedAt" : "null",
      "deletedAt" : "null",
      "address" : "[B@7af25dd1",
      "reward" : {
        "createdAt" : "2019-01-24T12:36:40.134-08:00",
        "updatedAt" : "null",
        "deletedAt" : "null",
        "type" : {
          "createdAt" : "2019-01-24T12:36:40.087-08:00",
          "updatedAt" : "null",
          "deletedAt" : "null",
          "audience" : "PROVIDENCE",
          "type" : "EVEN"
        },
        "pool" : {
          "createdAt" : "2019-01-24T12:36:40.133-08:00",
          "updatedAt" : "null",
          "deletedAt" : "null",
          "cryptoKeyPair" : {
            "createdAt" : "2019-01-24T12:36:40.133-08:00",
            "updatedAt" : "null",
            "deletedAt" : "null",
            "publicKey" : "[B@5878544f"
          },
          "transactions" : [ ]
        },
        "metadatas" : [ ]
      },
      "prereq" : [ ]
    },
    "cryptoKeyPair" : {
      "createdAt" : "2019-01-24T12:36:40.128-08:00",
      "updatedAt" : "null",
      "deletedAt" : "null",
      "publicKey" : "[B@dedad8a"
    },
    "distributionFeeReward" : {
      "createdAt" : "2019-01-24T12:36:40.128-08:00",
      "updatedAt" : "null",
      "deletedAt" : "null",
      "type" : {
        "createdAt" : "2019-01-24T12:36:40.082-08:00",
        "updatedAt" : "null",
        "deletedAt" : "null",
        "audience" : "PROVIDENCE",
        "type" : "SINGLE"
      },
      "pool" : {
        "createdAt" : "2019-01-24T12:36:40.128-08:00",
        "updatedAt" : "null",
        "deletedAt" : "null",
        "cryptoKeyPair" : {
          "createdAt" : "2019-01-24T12:36:40.128-08:00",
          "updatedAt" : "null",
          "deletedAt" : "null",
          "publicKey" : "[B@5c58467e"
        },
        "transactions" : [ ]
      },
      "metadatas" : [ ]
    }
  }, {
    "createdAt" : "2019-01-24T12:36:40.103-08:00",
    "updatedAt" : "null",
    "deletedAt" : "null",
    "parentChallenge" : "null",
    "challengeSettings" : {
      "createdAt" : "2019-01-24T12:36:40.097-08:00",
      "updatedAt" : "null",
      "deletedAt" : "null",
      "name" : "TESTname1",
      "description" : "TESTdescription1",
      "imageUrl" : "TESTimageUrl1",
      "sponsorName" : "TESTsponsorName1",
      "expiration" : "2019-01-25T12:36:40.075-08:00",
      "shareExpiration" : "2019-01-25T12:36:40.075-08:00",
      "admin" : 1,
      "offChain" : false,
      "maxShares" : 100,
      "maxRewards" : "null",
      "maxDistributionFeeReward" : "null",
      "maxSharesPerReceivedShare" : "null",
      "maxDepth" : "null",
      "maxNodes" : "null"
    },
    "subChallenges" : [ ],
    "completionCriteria" : {
      "createdAt" : "2019-01-24T12:36:40.103-08:00",
      "updatedAt" : "null",
      "deletedAt" : "null",
      "address" : "[B@7af25dd1",
      "reward" : {
        "createdAt" : "2019-01-24T12:36:40.103-08:00",
        "updatedAt" : "null",
        "deletedAt" : "null",
        "type" : {
          "createdAt" : "2019-01-24T12:36:40.087-08:00",
          "updatedAt" : "null",
          "deletedAt" : "null",
          "audience" : "PROVIDENCE",
          "type" : "EVEN"
        },
        "pool" : {
          "createdAt" : "2019-01-24T12:36:40.103-08:00",
          "updatedAt" : "null",
          "deletedAt" : "null",
          "cryptoKeyPair" : {
            "createdAt" : "2019-01-24T12:36:40.102-08:00",
            "updatedAt" : "null",
            "deletedAt" : "null",
            "publicKey" : "[B@5c65872d"
          },
          "transactions" : [ ]
        },
        "metadatas" : [ ]
      },
      "prereq" : [ ]
    },
    "cryptoKeyPair" : {
      "createdAt" : "2019-01-24T12:36:40.097-08:00",
      "updatedAt" : "null",
      "deletedAt" : "null",
      "publicKey" : "[B@3f02c814"
    },
    "distributionFeeReward" : {
      "createdAt" : "2019-01-24T12:36:40.098-08:00",
      "updatedAt" : "null",
      "deletedAt" : "null",
      "type" : {
        "createdAt" : "2019-01-24T12:36:40.082-08:00",
        "updatedAt" : "null",
        "deletedAt" : "null",
        "audience" : "PROVIDENCE",
        "type" : "SINGLE"
      },
      "pool" : {
        "createdAt" : "2019-01-24T12:36:40.097-08:00",
        "updatedAt" : "null",
        "deletedAt" : "null",
        "cryptoKeyPair" : {
          "createdAt" : "2019-01-24T12:36:40.097-08:00",
          "updatedAt" : "null",
          "deletedAt" : "null",
          "publicKey" : "[B@713c2b07"
        },
        "transactions" : [ ]
      },
      "metadatas" : [ ]
    }
  } ],
  "headers" : {
    "X-Powered-By" : "AWS Lambda & Serverless"
  },
  "base64Encoded" : false
}
```

## GET (requestData object must pass data in as query parameters instead of request body) All Balances For a Challenge

This API is called by a challenge sponsor in order to retrieve all balances being held by other users on that challenge

#### Route

/challenge/balances

#### Method

GET (requestData object must pass data in as query parameters instead of request body)

#### Parameters

1. user
   * The user making the API call
   * Datatype: UserAccount
1. requestData
   * Datatype: Object
   * Contains the following fields:
      * secretKey - the caller's API secret key for verification
1. id
  * The ID of the challenge for which to retrieve the balances
  * Datatype: Int
   
#### Sample Response

```json
{
  "statusCode" : 200,
  "body" : {
    "challengeId" : 4,
    "emailToChallengeBalances" : {
      "dev1@ncnt.io" : 2,
      "dev0@ncnt.io" : 98
    }
  },
  "headers" : {
    "X-Powered-By" : "AWS Lambda & Serverless"
  },
  "base64Encoded" : false
}
```

## Create Challenge

This API is called in order to create a new challenge

#### Route

/challenge

#### Method

POST

#### Parameters

1. user
   * The user making the API call
   * Datatype: UserAccount
1. requestData
   * Datatype: Object
   * Contains the following fields:
      * secretKey - the caller's API secret key for verification
      * body - a data object containing a "challengeNamespace field", which should contain the appropriate data for the challenge to be created. 
               For more information on the required and optional data fields for a Challenge, read about the [Challenge Model](./Models.md)
   
#### Sample Response

```json
{
  "statusCode" : 200,
  "body" : {
    "createdAt" : "2019-01-24T20:36:34.740Z",
    "updatedAt" : "null",
    "deletedAt" : "null",
    "parentChallenge" : "null",
    "challengeSettings" : {
      "createdAt" : "2019-01-24T20:36:34.728Z",
      "updatedAt" : "null",
      "deletedAt" : "null",
      "name" : "TESTname0",
      "description" : "TESTdescription0",
      "imageUrl" : "TESTimageUrl0",
      "sponsorName" : "TESTsponsorName0",
      "expiration" : "2019-01-25T20:36:34.110Z",
      "shareExpiration" : "2019-01-25T20:36:34.110Z",
      "admin" : 1,
      "offChain" : false,
      "maxShares" : 100,
      "maxRewards" : "null",
      "maxDistributionFeeReward" : "null",
      "maxSharesPerReceivedShare" : "null",
      "maxDepth" : "null",
      "maxNodes" : "null"
    },
    "subChallenges" : [ ],
    "completionCriteria" : {
      "createdAt" : "2019-01-24T20:36:34.739Z",
      "updatedAt" : "null",
      "deletedAt" : "null",
      "address" : "[B@57d4384e",
      "reward" : {
        "createdAt" : "2019-01-24T20:36:34.738Z",
        "updatedAt" : "null",
        "deletedAt" : "null",
        "type" : {
          "createdAt" : "2019-01-24T20:36:34.736Z",
          "updatedAt" : "null",
          "deletedAt" : "null",
          "audience" : "PROVIDENCE",
          "type" : "EVEN"
        },
        "pool" : {
          "createdAt" : "2019-01-24T20:36:34.738Z",
          "updatedAt" : "null",
          "deletedAt" : "null",
          "cryptoKeyPair" : {
            "createdAt" : "2019-01-24T20:36:34.737Z",
            "updatedAt" : "null",
            "deletedAt" : "null",
            "publicKey" : "[B@59fb433b"
          },
          "transactions" : [ ]
        },
        "metadatas" : [ ]
      },
      "prereq" : [ ]
    },
    "cryptoKeyPair" : {
      "createdAt" : "2019-01-24T20:36:34.725Z",
      "updatedAt" : "null",
      "deletedAt" : "null",
      "publicKey" : "[B@b97f950"
    },
    "distributionFeeReward" : {
      "createdAt" : "2019-01-24T20:36:34.730Z",
      "updatedAt" : "null",
      "deletedAt" : "null",
      "type" : {
        "createdAt" : "2019-01-24T20:36:34.723Z",
        "updatedAt" : "null",
        "deletedAt" : "null",
        "audience" : "PROVIDENCE",
        "type" : "SINGLE"
      },
      "pool" : {
        "createdAt" : "2019-01-24T20:36:34.727Z",
        "updatedAt" : "null",
        "deletedAt" : "null",
        "cryptoKeyPair" : {
          "createdAt" : "2019-01-24T20:36:34.725Z",
          "updatedAt" : "null",
          "deletedAt" : "null",
          "publicKey" : "[B@79bf9cdd"
        },
        "transactions" : [ ]
      },
      "metadatas" : [ ]
    }
  },
  "headers" : {
    "X-Powered-By" : "AWS Lambda & Serverless"
  },
  "base64Encoded" : false
}
```

## Share Challenge

This API is called in order to share a challenge with another user, and invite that user to either participate in the challenge, or send it on.

#### Route

/challenge/share

#### Method

PATCH

#### Parameters

1. user
   * The user making the API call
   * Datatype: UserAccount
1. requestData
   * Datatype: Object
   * Contains the following fields:
      * secretKey - the caller's API secret key for verification
      * body - an object containing the following fields:
         * challengeId - the Integer ID of the challenge to be shared
         * publicKeyToShareWith - the optional String representation of the public key address of the recipient's crypto wallet, if recipient email is not known
         * shares - the Integer number of invitations to send to the recipient
         * expiration - the optional DateTime representation of the expiration of the invitations
         * emailToShareWith - the optional email address of the recipient, if the crypto public key is not known
   
#### Sample Response

```json
{
  "statusCode" : 200,
  "body" : {
    "transactions" : [ {
      "createdAt" : "2019-01-24T20:36:46.377Z",
      "updatedAt" : "null",
      "deletedAt" : "null",
      "from" : "[B@248fa38f",
      "to" : "[B@14a5ac9f",
      "action" : {
        "createdAt" : "2019-01-24T20:36:46.376Z",
        "updatedAt" : "null",
        "deletedAt" : "null",
        "type" : "SHARE",
        "data" : 4,
        "dataType" : "Challenge"
      },
      "previousTransactionId" : "10",
      "metadatas" : [ {
        "createdAt" : "2019-01-24T20:36:46.377Z",
        "updatedAt" : "null",
        "deletedAt" : "null",
        "key" : "challengeId",
        "value" : "4"
      }, {
        "createdAt" : "2019-01-24T20:36:46.377Z",
        "updatedAt" : "null",
        "deletedAt" : "null",
        "key" : "offChain",
        "value" : "false"
      }, {
        "createdAt" : "2019-01-24T20:36:46.377Z",
        "updatedAt" : "null",
        "deletedAt" : "null",
        "key" : "shareExpiration",
        "value" : "2019-01-25T12:36:46.353-08:00"
      }, {
        "createdAt" : "2019-01-24T20:36:46.377Z",
        "updatedAt" : "null",
        "deletedAt" : "null",
        "key" : "maxShares",
        "value" : "3"
      } ]
    } ]
  },
  "headers" : {
    "X-Powered-By" : "AWS Lambda & Serverless"
  },
  "base64Encoded" : false
}

```

## Redeem Challenge

This API is called by the sponsor in order to trigger challenge redemption for a single participant

#### Route

/challenge/redeem

#### Method

PATCH

#### Parameters

1. user
   * The user making the API call
   * Datatype: UserAccount
1. requestData
   * Datatype: Object
   * Contains the following fields:
      * secretKey - the caller's API secret key for verification
      * body - a data object containing the following fields:
         * completerPublicKey - a String representation of the redeemer's public key
         * challengeId - the challenge for which the recipient is to be redeemed
   
#### Sample Response

```json
{
  "statusCode" : 200,
  "body" : {
    "transactions" : [ {
      "createdAt" : "2019-01-24T20:36:37.630Z",
      "updatedAt" : "null",
      "deletedAt" : "null",
      "from" : "[B@cf9a52a",
      "to" : "[B@441a52ec",
      "action" : {
        "createdAt" : "2019-01-24T20:36:37.629Z",
        "updatedAt" : "null",
        "deletedAt" : "null",
        "type" : "PAYOUT",
        "data" : 1,
        "dataType" : "Token"
      },
      "previousTransactionId" : "null",
      "metadatas" : [ {
        "createdAt" : "2019-01-24T20:36:37.629Z",
        "updatedAt" : "null",
        "deletedAt" : "null",
        "key" : "amount",
        "value" : "5.0"
      }, {
        "createdAt" : "2019-01-24T20:36:37.629Z",
        "updatedAt" : "null",
        "deletedAt" : "null",
        "key" : "notes",
        "value" : "Reward distribution"
      } ]
    }, {
      "createdAt" : "2019-01-24T20:36:37.631Z",
      "updatedAt" : "null",
      "deletedAt" : "null",
      "from" : "[B@cf9a52a",
      "to" : "[B@4fedba92",
      "action" : {
        "createdAt" : "2019-01-24T20:36:37.631Z",
        "updatedAt" : "null",
        "deletedAt" : "null",
        "type" : "PAYOUT",
        "data" : 1,
        "dataType" : "Token"
      },
      "previousTransactionId" : "null",
      "metadatas" : [ {
        "createdAt" : "2019-01-24T20:36:37.631Z",
        "updatedAt" : "null",
        "deletedAt" : "null",
        "key" : "amount",
        "value" : "5.0"
      }, {
        "createdAt" : "2019-01-24T20:36:37.631Z",
        "updatedAt" : "null",
        "deletedAt" : "null",
        "key" : "notes",
        "value" : "Reward distribution"
      } ]
    } ]
  },
  "headers" : {
    "X-Powered-By" : "AWS Lambda & Serverless"
  },
  "base64Encoded" : false
}
```

## Complete Challenge

This API is called by the sponsor in order to trigger redemption for a single participant and end the challenge

#### Route

/challenge/complete

#### Method

PATCH

#### Parameters

1. user
   * The user making the API call
   * Datatype: UserAccount
1. requestData
   * Datatype: Object
   * Contains the following fields:
      * secretKey - the caller's API secret key for verification
      * body - a data object containing the following fields:
          * completerPublicKey - a String representation of the redeemer's public key
          * challengeId - the challenge for which the recipient is to be redeemed
         
   
#### Sample Response

```json
{
  "statusCode" : 200,
  "body" : {
    "transactions" : [ {
      "createdAt" : "2019-01-24T20:36:36.785Z",
      "updatedAt" : "null",
      "deletedAt" : "null",
      "from" : "[B@2adfdec6",
      "to" : "[B@43850325",
      "action" : {
        "createdAt" : "2019-01-24T20:36:36.784Z",
        "updatedAt" : "null",
        "deletedAt" : "null",
        "type" : "PAYOUT",
        "data" : 1,
        "dataType" : "Token"
      },
      "previousTransactionId" : "null",
      "metadatas" : [ {
        "createdAt" : "2019-01-24T20:36:36.784Z",
        "updatedAt" : "null",
        "deletedAt" : "null",
        "key" : "amount",
        "value" : "5.0"
      }, {
        "createdAt" : "2019-01-24T20:36:36.784Z",
        "updatedAt" : "null",
        "deletedAt" : "null",
        "key" : "notes",
        "value" : "Reward distribution"
      } ]
    }, {
      "createdAt" : "2019-01-24T20:36:36.786Z",
      "updatedAt" : "null",
      "deletedAt" : "null",
      "from" : "[B@2adfdec6",
      "to" : "[B@7ac9b3cb",
      "action" : {
        "createdAt" : "2019-01-24T20:36:36.786Z",
        "updatedAt" : "null",
        "deletedAt" : "null",
        "type" : "PAYOUT",
        "data" : 1,
        "dataType" : "Token"
      },
      "previousTransactionId" : "null",
      "metadatas" : [ {
        "createdAt" : "2019-01-24T20:36:36.786Z",
        "updatedAt" : "null",
        "deletedAt" : "null",
        "key" : "amount",
        "value" : "5.0"
      }, {
        "createdAt" : "2019-01-24T20:36:36.786Z",
        "updatedAt" : "null",
        "deletedAt" : "null",
        "key" : "notes",
        "value" : "Reward distribution"
      } ]
    } ]
  },
  "headers" : {
    "X-Powered-By" : "AWS Lambda & Serverless"
  },
  "base64Encoded" : false
}
```

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