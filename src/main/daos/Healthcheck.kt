package main.daos

import framework.models.BaseIntEntity
import framework.models.BaseIntEntityClass
import framework.models.BaseIntIdTable
import kotlinserverless.framework.models.Handler
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
		map.put("dburl", Handler().getDbName())
		return map
	}
}

object Healthchecks : BaseIntIdTable("healthchecks") {
	var status = varchar("status", 20).uniqueIndex()
	var message = varchar("message", 100)
}