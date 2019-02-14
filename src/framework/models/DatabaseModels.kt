package framework.models

import org.jetbrains.exposed.dao.*
import org.joda.time.DateTime
import org.joda.time.DateTimeZone

fun currentUtc(): DateTime = DateTime.now(DateTimeZone.UTC)

abstract class BaseIntIdTable(name: String) : IntIdTable(name) {
    val createdAt = datetime("createdAt").clientDefault { currentUtc() }
    val updatedAt = datetime("updatedAt").nullable()
    val deletedAt = datetime("deletedAt").nullable()
}

abstract class BaseIntEntity(id: EntityID<Int>, table: BaseIntIdTable) : BaseObject, IntEntity(id) {
    val createdAt by table.createdAt
    var updatedAt by table.updatedAt
    var deletedAt by table.deletedAt

    override fun toMap(): MutableMap<String, Any?> {
        return mutableMapOf(
            Pair("id", idValue),
            Pair("createdAt", createdAt.toString()),
            Pair("updatedAt", updatedAt.toString()),
            Pair("deletedAt", deletedAt.toString())
        )
    }
}

interface BaseObject {
    fun toMap(): MutableMap<String, Any?>
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
            } else if (action.changeType == EntityChangeType.Removed) {
                try {
                    action.toEntity(this)?.deletedAt = currentUtc()
                } catch (e: Exception) {
                    //nothing much to do here
                }
            }
        }
    }
}

val BaseIntEntity.idValue: Int
    get() = this.id.value
