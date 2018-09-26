package kotlinserverless.main.users.services

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.services.SOAServiceInterface
import kotlinserverless.main.users.models.User

class UserHelloService: SOAServiceInterface<User> {
	fun execute(data: String?, params: Map<String, String>?) : SOAResult<String> {
		return SOAResult(SOAResultType.SUCCESS, null, "HELLO WORLD")
	}
}