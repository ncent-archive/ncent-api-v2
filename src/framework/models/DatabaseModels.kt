package framework.models

import com.fasterxml.jackson.databind.ObjectMapper
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.joda.time.DateTimeZone

fun currentUtc(): DateTime = DateTime.now(DateTimeZone.UTC)

abstract class BaseIntIdTable(name: String) : IntIdTable(name) {
    val createdAt = datetime("createdAt").clientDefault { currentUtc() }
    val updatedAt = datetime("updatedAt").nullable()
    val deletedAt = datetime("deletedAt").nullable()
}

abstract class BaseIntEntity(id: EntityID<Int>, table: BaseIntIdTable) : IntEntity(id) {
    val createdAt by table.createdAt
    var updatedAt by table.updatedAt
    var deletedAt by table.deletedAt

    override fun toString(): String {
        return transaction {
            return@transaction ObjectMapper().writeValueAsString(toMap())
        }
    }

    open fun toMap(): MutableMap<String, Any?> {
        return mutableMapOf(
            Pair("createdAt", createdAt.toString()),
            Pair("updatedAt", updatedAt.toString()),
            Pair("deletedAt", deletedAt.toString())
        )
    }
}

abstract class BaseNamespace {
    override fun toString(): String {
        return transaction {
            return@transaction ObjectMapper().writeValueAsString(toMap())
        }
    }

    abstract fun toMap(): MutableMap<String, Any?>
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
