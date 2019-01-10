package kotlinserverless.main.services.users

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType

object UserHelloService: SOAServiceInterface<String> {
	override fun execute() : SOAResult<String> {
		return SOAResult(SOAResultType.SUCCESS, null, "HELLO WORLD")
	}
}