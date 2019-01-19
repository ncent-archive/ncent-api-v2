package kotlinserverless.framework.controllers

import framework.models.BaseIntEntity
import kotlinserverless.framework.models.*
import kotlinserverless.framework.services.SOAResult
import main.daos.UserAccount
import main.helpers.ControllerHelper
import java.lang.NullPointerException
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
    fun <T : BaseIntEntity> defaultRouting(inputClass: String, outcls: Class<T>, requestData: ControllerHelper.RequestData, user: UserAccount?, restController: RestController<T, UserAccount>): SOAResult<*> {
        val method = (requestData.request.input[ControllerHelper.HTTP_METHOD] as String).toLowerCase()
        if(method == ControllerHelper.HTTP_POST)
            return restController.create(user, requestData)
        try {
            user!!
        } catch(e: NullPointerException) {
            throw NullPointerException("You must pass the user authentication header and value in order to use this.")
        }
        return when(method) {
            ControllerHelper.HTTP_GET -> {
                when {
                    requestData.body.containsKey("id") -> {
                        val id = requestData.body["id"].toString().toIntOrNull()

                        id ?: throw Exception("Id must be an integer")
                        restController.findOne(user, requestData, id.toString().toInt())
                    }
                    else -> {
                        return restController.findAll(user, requestData)
                    }
                }
            }
            ControllerHelper.HTTP_PUT -> {
                restController.update(user, requestData)
            }
            ControllerHelper.HTTP_DELETE -> {
                restController.delete(user, requestData, requestData.body["id"].toString().toInt())
            }
            ControllerHelper.HTTP_PATCH -> {
                restController.update(user, requestData)
            }

            else -> {
                throw Exception()
            }
        }
    }
}