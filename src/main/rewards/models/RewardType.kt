package main.rewards.models

import kotlinserverless.framework.models.BaseModel

data class RewardType(
        override var id: Int?,
        val type: String
) : BaseModel()