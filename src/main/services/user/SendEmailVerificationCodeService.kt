package main.services.user

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface

/**
 *
 */
object SendEmailVerificationCodeService: SOAServiceInterface<Boolean> {
    override fun execute(caller: Int?, params: Map<String, String>?) : SOAResult<Boolean> {
        throw NotImplementedError()
    }
}