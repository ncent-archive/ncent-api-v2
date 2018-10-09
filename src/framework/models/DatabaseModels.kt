package framework.models

import org.jetbrains.exposed.dao.*
import org.joda.time.DateTime
import org.joda.time.DateTimeZone

fun currentUtc(): DateTime = DateTime.now(DateTimeZone.UTC)

abstract class BaseIntIdTable(name: String) : IntIdTable(name) {
    val createdAt = datetime("createdAt").clientDefault { currentUtc() }
    val updatedAt = datetime("updatedAt").nullable()
}

abstract class BaseIntEntity(id: EntityID<Int>, table: BaseIntIdTable) : IntEntity(id) {
    val createdAt by table.createdAt
    var updatedAt by table.updatedAt
}

abstract class BaseIntEntityClass<E : BaseIntEntity>(table: BaseIntIdTable) : IntEntityClass<E>(table) {

    init {
        EntityHook.subscribe { action ->
            if (action.changeType == EntityChangeType.Updated) {
                try {
                    action.toEntity(this)?.updatedAt = currentUtc()
                } catch (e: Exception) {
                    //nothing much to do here
                }
            }
        }
    }
}

val BaseIntEntity.idValue: Int
    get() = this.id.value