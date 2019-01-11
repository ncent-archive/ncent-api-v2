package kotlinserverless.main.services.users

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType

object UserHelloService {
	fun execute() : SOAResult<String> {
		return SOAResult(SOAResultType.SUCCESS, null, "HELLO WORLD")
	}
}