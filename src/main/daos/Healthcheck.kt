package kotlinserverless.framework.healthchecks.models

import framework.models.BaseIntEntity
import framework.models.BaseIntEntityClass
import framework.models.BaseIntIdTable
import org.jetbrains.exposed.dao.EntityID

class Healthcheck(id: EntityID<Int>): BaseIntEntity(id, Healthchecks) {
	var status by Healthchecks.status
	var message by Healthchecks.message

	companion object: BaseIntEntityClass<Healthcheck>(Healthchecks) {
		fun findByStatus(status: String): Healthcheck {
			return Healthcheck.find { Healthchecks.status eq status }.first()
		}
	}
}

private object Healthchecks : BaseIntIdTable("healthchecks") {
	var status = varchar("status", 10)
	var message = varchar("message", 100)
}