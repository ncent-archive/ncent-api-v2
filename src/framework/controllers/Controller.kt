package kotlinserverless.framework.controllers

import framework.models.BaseIntEntity
import kotlinserverless.framework.models.*
import kotlinserverless.framework.services.SOAResult
import main.daos.UserAccount
import main.helpers.ControllerHelper
import kotlin.math.ceil

/**
 * Controller that receives a request and reply with a message of type {@link M}
 * @param M Model type
 */
interface Controller<M> {
    /**
     * Http router, it receives a class type to return, a request and service to automatically execute and return a response
     * @param incls Class input type
     * @param outcls Class return type
     * @param request Http Client request
     * @param service CRUD service to execute
     */
    fun <T : BaseIntEntity> defaultRouting(inputClass: String, outcls: Class<T>, request: Request, user: UserAccount, restController: RestController<T, UserAccount>): SOAResult<*> {
		val resource = ControllerHelper.getResource(request)
        val headers: Map<String, Any> = ControllerHelper.getHeaders(request)
        val pathParameters: Map<String, Any> = ControllerHelper.getPathParameters(request)
        val queryParameters: Map<String, Any> = ControllerHelper.getQueryStringParameters(request)
        val incls = Class.forName(inputClass)

        return when((request.input[ControllerHelper.HTTP_METHOD] as String).toLowerCase()) {
            ControllerHelper.HTTP_GET -> {
                when {
                    resource != null && resource.endsWith("findOne", true) -> restController.findOne(user, queryParameters)
                    pathParameters.containsKey("id") -> {
                        val id = pathParameters["id"]

                        when (id) {
                            is Int -> restController.findOne(user, queryParameters, id.toString().toInt())
                            else -> throw Exception("Id must be an integer")
                        }
                    }
                    else -> {
                        return restController.findAll(user, queryParameters)
                    }
                }
            }
            ControllerHelper.HTTP_POST -> {
                restController.create(user, queryParameters)
            }
            ControllerHelper.HTTP_PUT -> {
                restController.update(user, queryParameters)
            }
            ControllerHelper.HTTP_DELETE -> {
                restController.delete(user, queryParameters, request.input["idValue"].toString().toInt())
            }
            ControllerHelper.HTTP_PATCH -> {
                restController.update(user, queryParameters)
            }

            else -> {
                throw Exception()
            }
        }
    }
}