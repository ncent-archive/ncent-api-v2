package main.services.challenge

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.*

/**
 * Trigger a challenge state change to active.
 */
object ActivateChallengeService {
    fun execute(caller: UserAccount, challengeId: Int) : SOAResult<Transaction> {
        return ChangeChallengeStateService.execute(caller, challengeId, ActionType.ACTIVATE)
    }
}