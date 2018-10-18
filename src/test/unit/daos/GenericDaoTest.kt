package test.unit.daos

import framework.models.BaseIntEntity
import framework.models.BaseIntEntityClass
import framework.models.BaseIntIdTable
import framework.models.idValue
import io.kotlintest.*
import io.kotlintest.data.forall
import io.kotlintest.matchers.instanceOf
import io.kotlintest.specs.StringSpec
import io.kotlintest.tables.row
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import kotlinserverless.framework.models.Handler
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import java.io.File
import kotlin.reflect.full.createInstance


@ExtendWith(MockKExtension::class)
class GenericDaoTest : StringSpec() {
    var daoObjectsAndTables = mutableMapOf<BaseIntEntity, Class<BaseIntIdTable>>()
    val fakeTable = EntityID(1, IntIdTable::class.createInstance())

    override fun beforeTest(description: Description): Unit {
        Handler.connectToDatabase()
        File("./src/main/daos/")
            .list()
            .forEach { it ->
                val fileName = "main.daos." + it.toString().split(".")[0]
                val constructor = Class.forName(fileName).kotlin.constructors
                var tableClazz = Class.forName(fileName + "s")
                daoObjectsAndTables.put(
                    constructor.first().call(fakeTable) as BaseIntEntity,
                    tableClazz as Class<BaseIntIdTable>
                )
            }
    }

    init {
        "daos" {
            forall(
                *daoObjectsAndTables.map { it -> row(it.key) }.toTypedArray()
            ) {
                daoObject: BaseIntEntity ->
                var daoInstance = transaction {
//                    SchemaUtils.create(daoObjectsAndTables.get(daoObject)!!.kotlin.objectInstance!!)
//                    BaseIntEntity<daoObject::class>(daoObjectsAndTables.get(daoObject)!!.kotlin.objectInstance!!)
//                    //daoObject::klass..call("new", fakeTable)
//                    return@transaction daoObject.klass.new { fakeTable }
                   // instance.id.value shouldBe 1
                    //daoObject.deletedAt = DateTime.now()
                }
//                daoObject.updatedAt shouldNotBe null
//                transaction {
//                    val time = DateTime.now()
//                    daoObject.updatedAt = time
//                    daoObject.refresh(true)
//                    daoObject.updatedAt shouldBe time
//                }
            }
        }
    }
}
