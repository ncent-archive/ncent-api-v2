package main.rewards.models

import kotlinserverless.framework.models.BaseModel

/**
 * RewardType signifies the way the rewards will be distributed amongst the entire chain or the providence chain
 *
 * @property id
 * @property audience example: [PROVIDENCE, FULL]
 * @property type example: [SINGLE, EVEN, TIERED, LOGARITHMIC, EXPONENTIAL_UP, EXPONENTIAL_DOWN]
 */
data class RewardType(
        override var id: Int?,
        val audience: String,
        val type: String
) : BaseModel()