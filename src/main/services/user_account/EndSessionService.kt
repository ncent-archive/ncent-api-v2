package main.services.user_account

import framework.services.DaoService
import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.Session
import main.daos.Sessions
import org.joda.time.DateTime

/**
 * Used to end a session (cache)
 */
class EndSessionService: SOAServiceInterface<Session> {
    private val daoService = DaoService<Session>()
    override fun execute(caller: Int?, key: String?) : SOAResult<Session> {
        return daoService.execute {
            var session = Session.find { Sessions.sessionKey eq key!! }.first()
            if(session.expiration > DateTime.now()) {
                session.expiration = DateTime.now()
            }
            return@execute session
        }
    }
}