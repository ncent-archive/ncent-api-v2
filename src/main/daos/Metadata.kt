package main.daos

import framework.models.BaseIntEntity
import framework.models.BaseIntEntityClass
import framework.models.BaseIntIdTable
import org.jetbrains.exposed.dao.EntityID

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
}

object Metadatas : BaseIntIdTable("metadatas") {
    val key = varchar("key", 256)
    val value = varchar("value", 256)
}

data class MetadatasNamespace(val key: String, val value: String)

<<<<<<< HEAD
data class MetadatasListNamespace(val metadatas: List<MetadatasNamespace>)
=======
data class MetadatasListNamespace(val metadatas: List<MetadatasNamespace>)
>>>>>>> Temporary progress on Getter services and also getting lists to work with the DB and query better
