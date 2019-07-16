package main.helpers

object Properties {

    val regionName: String = "us-east-2"
    val cognitoUserPoolId: String = "us-east-2_VDWBAgbOn"
    val jwksUrl = "https://cognito-idp.$regionName.amazonaws.com/$cognitoUserPoolId/.well-known/jwks.json"
    val jwtTokenIssuer = "https://cognito-idp.$regionName.amazonaws.com/$cognitoUserPoolId"
}