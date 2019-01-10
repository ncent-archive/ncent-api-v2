package main.services.challenge

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import java.lang.Exception

/**
 * Add a sub challenge to a challenge.
 */
object AddSubChallengeService {
    fun execute(caller: UserAccount, subChallengeNamespace: ChallengeNamespace, challengeId: Int, subChallengeType: SubChallengeType) : SOAResult<SubChallenge> {
        // TODO validate the sub challenge expiration, amounts, same parent id, etc...must accomidate for parent challenge
        val subChallengeResult = GenerateChallengeService.execute(caller, subChallengeNamespace, null)
        if(subChallengeResult.result != SOAResultType.SUCCESS)
            throw Exception(subChallengeResult.message)

        val challengeFound = Challenge.findById(challengeId)!!

        val subChallengeId = SubChallenges.insertAndGetId {
            it[subChallenge] = subChallengeResult.data!!.id
            it[type] = subChallengeType
        }

        ChallengeToSubChallenges.insert {
            it[challenge] = challengeFound.id
            it[subChallenge] = subChallengeId
        }

        return SOAResult(SOAResultType.SUCCESS, null, SubChallenge.findById(subChallengeId)!!)
    }
}