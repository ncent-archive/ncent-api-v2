package kotlinserverless.main.users.models

import kotlinserverless.framework.models.BaseModel

/**
 * Basic fields that a User needs
 * @property id User id
 * @property email User email
 * @property firstname User first name
 * @property lastname User last name
 */
data class User(
		override var id: Int?,
		var email: String,
		var firstname: String,
		var lastname: String
) : BaseModel()