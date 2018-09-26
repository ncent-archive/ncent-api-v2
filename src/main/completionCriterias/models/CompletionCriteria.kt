package main.completionCriterias.models

import kotlinserverless.framework.models.BaseModel

/**
 * Currently only housing who can validate the completion of a challenge
 * Eventually this will house things like smart contract completers.
 *
 * @property id
 * @property address The address which can trigger validation of completion
 */
data class CompletionCriteria(
        override var id: Int?,
        val address: String

) : BaseModel()