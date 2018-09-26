package main.rewards.models

import kotlinserverless.framework.models.BaseModel


/**
 * Reward
 *
 * @property id
 * @property type RewardType
 * @property pool List<RewardPool>
 */
data class Reward(
        override var id: Int?,
        val type: RewardType,
        val pool: List<RewardPool>
) : BaseModel()