package kotlinserverless.framework.healthchecks

import kotlinserverless.framework.MyException

class InvalidEndpoint(code: Int = 400, message: String = "This is an unhealthy endpoint") : MyException(code, message)