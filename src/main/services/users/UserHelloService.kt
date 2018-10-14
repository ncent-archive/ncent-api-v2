package kotlinserverless.main.users.services

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.services.SOAServiceInterface

class UserHelloService: SOAServiceInterface<String> {
	override fun execute(caller: Int?, data: String?, params: Map<String, String>?) : SOAResult<String> {
		return SOAResult(SOAResultType.SUCCESS, null, "HELLO WORLD")
	}
}