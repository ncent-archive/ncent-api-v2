package main.daos

import com.fasterxml.jackson.databind.ObjectMapper
import framework.models.BaseIntEntity
import framework.models.BaseIntEntityClass
import framework.models.BaseIntIdTable
import org.jetbrains.exposed.dao.EntityID
import kotlin.reflect.full.primaryConstructor

/**
 * Metadata will be a key-value store
 *
 * @property id
 * @property key
 * @property value
 */
class Metadata(id: EntityID<Int>) : BaseIntEntity(id, Metadatas) {
    companion object : BaseIntEntityClass<Metadata>(Metadatas)

    var key by Metadatas.key
    var value by Metadatas.value

    override fun toMap(): MutableMap<String, Any?> {
        var map = super.toMap()
        map.put("key", key)
        map.put("value", value)
        return map
    }
}

object Metadatas : BaseIntIdTable("metadatas") {
    val key = varchar("md_key", 256)
    val value = varchar("md_value", 256)
}

data class MetadatasNamespace(val key: String, val value: String)