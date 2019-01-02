package kotlinserverless.framework.models

import com.amazonaws.services.lambda.runtime.Context
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import framework.models.BaseIntEntity
import framework.models.BaseIntIdTable
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import java.io.IOException
import java.net.URL
import kotlin.math.ceil

/**
 * Represents a client Request
 * @property input JSON object with all the request data described in https://serverless.com/framework/docs/providers/aws/events/apigateway/
 * @property context Lambda function Metadata
 */
interface Request {
    var input: Map<String, Any>
    var context: Context
}

/**
 * Request Dispatcher route to a concrete function based on a regex
 * @property regex Regex that needs to satisfy to route the request
 * @property outputModel Name of model class to expect to input (usually a namespace)
 * @property outputModel Name of model class to expect to return
 * @property controller Name of controller class
 * @constructor Creates an empty object with empty values
 */
data class Route(var regex: String, var inputModel: String, var outputModel: String, var controller: String) {
    constructor(): this("", "", "", "")
}

/**
 * List of routes
 * @property List of routes
 * @constructor emptyList
 */
data class Routes(var routes: List<Route>) {
    constructor(): this(emptyList())
}

/**
 * Represents an APIGateway Request (implements {@link Request})
 * @property input JSON object with all the request data described in https://serverless.com/framework/docs/providers/aws/events/apigateway/
 * @property context Lambda function Metadata
 */
class ApiGatewayRequest(override var input:Map<String, Any>, override var context: Context): Request


// ------------------------------------------------------
// User management
// ------------------------------------------------------

/**
 * It's a way to keep a canonical Datetime serializer
 */
class DateTimeSerializer @JvmOverloads constructor(t: Class<DateTime>? = null) : StdSerializer<DateTime>(t) {

    @Throws(IOException::class, JsonProcessingException::class)
    override fun serialize(value: DateTime, gen: JsonGenerator, arg2: SerializerProvider) {
        gen.writeString(formatter.print(value))
    }

    private val formatter = ISODateTimeFormat.basicDateTime()
}

// ------------------------------------------------------
// HTTP and W3C compliant models for HATEOAS/HAL
// ------------------------------------------------------

/**
 * A link relation indicating what is the first, last, previous and next resource
 * This is a good practice for self discovery using HAL http://stateless.co/hal_specification.html
 * @property value Enum value
 */
enum class LinkRel(val value: String) {
    NEXT("next"),
    FIRST("first"),
    LAST("last"),
    SELF("self")
}

/**
 * Resource link
 * @property rel Type of link
 * @property url URL String
 */
data class Link(var rel: LinkRel, var url: URL)

/**
 * Metadata of an specific page, in case of HAL it's always good practice to return context data
 * @param limit Max amount of objects to show per page (like SQL LIMIT)
 * @param offset From which element we start to count
 * @param count Total elements
 * @property totalPages Total pages that it's basically the ceil(COUNT/LIMIT)
 * @property pageNumber Current page that it's basically the ceil(OFFSET+1/LIMIT)
 * @property totalElements It's equals to COUNT
 */
class PageMetadata(limit: Int, offset: Int, count: Int) {
    val totalPages: Int = ceil(count.toDouble() / limit.toDouble()).toInt()
    val pageNumber: Int = ceil((offset.toDouble() + 1) / limit.toDouble()).toInt()
    val totalElements: Int = count

    val first: Boolean
        get() = pageNumber == 1

    val last: Boolean
        get() = pageNumber == totalPages

    override fun toString(): String {
        return "PageMetadata(totalPages=$totalPages, pageNumber=$pageNumber, " +
                "totalElements=$totalElements, first=$first, last=$last)"
    }
}

/**
 * In case of multiple elements we reply with a page, that includes the data and metadata of a set of objects
 * @param sort Map of values to sort (based on the URL params, that means that we don't include ?page & ?size)
 * @param limit Max amount of objects to show per page (like SQL LIMIT)
 * @param offset From which element we start to count
 * @property content List of objects
 * @property links HAL links
 * @property sort Way to sort our results based on URL params
 * @property metadata HAL metadata
 */
class Page<T>(
        sort: Map<String, Any>,
        limit: Int,
        offset: Int,
        count: Int,
        var content: List<T>,
        var links: List<Link> = emptyList()
): BaseIntIdTable("pages") {

    var sort: Map<String, Any> = sort
        get() = field.minus(arrayOf("page", "size"))

    val metadata: PageMetadata = PageMetadata(limit, offset, count)


    override fun toString(): String {
        return "Page(content=$content, sort=$sort, links=$links, metadata=$metadata)"
    }
}

/**
 * Desired pagination
 * @property page Page Number to present
 * @property size Size of the pages
 */
data class Pagination(var page: Int, var size: Int)
