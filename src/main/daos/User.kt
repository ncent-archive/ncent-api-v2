package main.daos

import framework.models.BaseIntEntity
import framework.models.BaseIntEntityClass
import framework.models.BaseIntIdTable
import org.jetbrains.exposed.dao.*

/**
 * Basic fields that a User needs
 * @property id User id
 * @property email User email
 * @property firstname User first name
 * @property lastname User last name
 */
class User(id: EntityID<Int>) : BaseIntEntity(id, Users) {
	companion object : BaseIntEntityClass<User>(Users)

	var email by Users.email
	var firstname by Users.firstname
	var lastname by Users.lastname
}

object Users : BaseIntIdTable("users") {
	val email = varchar("email", 50).uniqueIndex()
	val firstname = varchar("firstname", 20)
	val lastname = varchar("lastname", 20)
}