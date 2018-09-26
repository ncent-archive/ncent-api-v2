package main.rewards.models

import kotlinserverless.framework.models.BaseModel
import main.tokens.models.Token
import main.transactions.models.Transaction

/**
 * RewardPool; model representing transactions related to a particular reward
 * The pool of all of the RewardPool (transactions) signifies the tokens available.
 *
 * @property id
 * @property reward linking back to a particular reward
 * @property transaction particular transaction for this pool
 */
data class RewardPool(
        override var id: Int?,
        val reward: Reward,
        val transaction: Transaction<Token>
) : BaseModel()