package test.unit.daos

import framework.models.BaseIntEntity
import framework.models.idValue
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import io.kotlintest.Description
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.StringSpec
import io.kotlintest.tables.headers
import io.kotlintest.tables.row
import io.kotlintest.tables.table
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.junit.jupiter.api.extension.ExtendWith
import kotlinserverless.framework.models.Handler
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.reflections.Reflections




@ExtendWith(MockKExtension::class)
class GenericDaoTest : StringSpec() {
//    lateinit var reflections : Reflections
//    lateinit var allClasses: Set<Class<out BaseIntEntity>>
//
//    override fun beforeTest(description: Description): Unit {
//        Handler(mockk()).connectToDatabase()
//        reflections = Reflections("main.daos")
//        allClasses = reflections.getSubTypesOf(BaseIntEntity::class.java)
//    }
//
//    init {
//        "daos" {
//            // TODO -- figure out how table works
//            table(
//                headers("daoClass"),
//                *allClasses.map { it -> row(it) }
//            ).forAll {
//                daoClass: Class<out BaseIntEntity> ->
//                println(daoClass)
//            }
//        }
//    }
}
