package kotlinserverless.framework.healthchecks.models

import kotlinserverless.framework.models.BaseModel

data class Healthcheck(var status: String, var message: String = "default"): BaseModel() {
	constructor(): this("unhealthy", "default")
}