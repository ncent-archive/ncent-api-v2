package main.services.user_account

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.helpers.Properties
import com.auth0.jwk.GuavaCachedJwkProvider
import com.auth0.jwk.UrlJwkProvider
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import com.auth0.jwt.algorithms.Algorithm
import java.net.URL
import java.security.interfaces.RSAKey

/**
 * Validate the accuracy of the passed JWT
 */
object ValidateJWTService {
    fun execute(token: String) : SOAResult<DecodedJWT> {
        var result = SOAResult<DecodedJWT>(SOAResultType.FAILURE, null, null)
        // Decode the key and set the kid
        val decodedJwtToken = JWT.decode(token)
        println("here1");
        val kid = decodedJwtToken.keyId
        println("here2");
        val provider = UrlJwkProvider(URL(Properties.jwksUrl))
        println("here3");

        // Let's cache the result from Cognito for the default of 10 hours
        println("here4 ${Properties.jwksUrl}");

        val jwk = try {
            provider.get(kid)
        } catch (e: Exception) {
            println("Exception: $e")
            null
        }
        
        println("here5");

        val algorithm = Algorithm.RSA256(jwk?.publicKey as RSAKey)
        println("here6");
        val verifier = JWT.require(algorithm)
                .withIssuer(Properties.jwtTokenIssuer)
                .build() //Reusable verifier instance
        println("here7");
        val jwt = try {
            verifier.verify(token)
        } catch (e: Exception) {
            false
        }
        println("here8");
        if(jwt != null){
            result.data = jwt as DecodedJWT
            result.result = SOAResultType.SUCCESS
        } else {
            result.message = "Invalid JWT"
        }
        println("here9");
        return result
    }
}