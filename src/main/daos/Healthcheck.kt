package main.daos

import framework.models.BaseIntEntity
import framework.models.BaseIntEntityClass
import framework.models.BaseIntIdTable
import framework.models.idValue
import org.jetbrains.exposed.dao.EntityID

class Healthcheck(id: EntityID<Int>): BaseIntEntity(id, Healthchecks) {
	var status by Healthchecks.status
	var message by Healthchecks.message

	companion object: BaseIntEntityClass<Healthcheck>(Healthchecks) {
		fun findByStatus(status: String): Healthcheck {
			return Healthcheck.find { Healthchecks.status eq status }.first()
		}
	}

	override fun toMap(): MutableMap<String, Any?> {
		var map = super.toMap()
		map.put("status", status)
		map.put("message", message)
		return map
	}
}

object Healthchecks : BaseIntIdTable("healthchecks") {
	var status = varchar("status", 10)
	var message = varchar("message", 100)
}