package main.daos

import framework.models.BaseIntEntity
import framework.models.BaseIntEntityClass
import framework.models.BaseIntIdTable
import org.jetbrains.exposed.dao.EntityID

/**
 * Session Information
 * @property sessionKey
 * @property expiration
 */
class Session(id: EntityID<Int>) : BaseIntEntity(id, Sessions) {
    companion object : BaseIntEntityClass<Session>(Sessions)

    var sessionKey by Sessions.sessionKey
    var expiration by Sessions.expiration
}

object Sessions : BaseIntIdTable("sessions") {
    val sessionKey = varchar("session_key", 256)
    // TODO: look into how this can be done better
    val expiration = datetime("expiration")
}