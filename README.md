# nCent (API)

 * [Overview](#Overview)
 * [Installation](#Install)
 * [Deploying](#Deploying)
 * [CI/CD](#CI/CD)
 * [Contribute](#Contributing)
 * [Author](#Author)

## Overview

- nCent Api
- This api is to be used to interact with the nCent protocol.
- Currently the logic is running via a traditional application structure.
    - We will be moving most of the protocol logic into a blockchain
- Api Reference docs can be found [here](docs/API%20Reference.md)
- Model docs can be found [here](docs/Models.md)
- UML Diagrams can be found [here](docs/UML)


### Tech stack:
- Serverless, Kotlin, jUnit, exposed (mysql, auroraserverless), circleci

## Install

NOTE: Make sure you're using Java 8 (v1.8.*)

1. Install it using npm:
  ```shell
  git clone git@github.com:thejnaut1/ncnt.git
  cd ncnt/kotlin/kotlin-serverless
  npm install serverless -g
  npm init -f
  npm install
  ```
  
2. Create your project in serverless.com

3. To configure your AWS credentials execute serverless config credentials --provider aws --key EXAMPLE --secret EXAMPLEKEY

4. mvn clean install

5. ```shell
   ./start.sh local
   ```
   
## Deploying
1. [Create AWS Serverless Aurora RDS DB](https://aws.amazon.com/getting-started/tutorials/configure-connect-serverless-mysql-database-aurora/)
    - Follow [Step 1, Step 2]
    - Note: Aurora serverless DBs can only be accessed from within the VPC, thus you cannot access it from your local server even if you change the inbound capabilities
    - Must access via EC2, Lambda Functions, or Cloud9IDE. EC2 route is described as follows.
2. [Create EC2 Instance to Access RDS DB](https://docs.aws.amazon.com/efs/latest/ug/gs-step-one-create-ec2-resources.html)
    - Note: Use same VPC/Security Group as the RDS DB
3. [Update Security group to have access between ec2 and rds](https://aws.amazon.com/getting-started/tutorials/configure-connect-serverless-mysql-database-aurora/)
    - Follow [Step 4] but note below:
    - Edit inbound to add SSH from all and MYSQL/Aurora from all
4. Add ncnt database to the newly created db
    - Connect to the ec2 instance
    - ```shell 
      ssh -i <path to pem file> ec2-user@<ec2 public url>
      ```
    - Update yum
    - ```shell
      sudo yum update
      ```
    - Install mysql if it doesn’t exist (check by using mysql command)
    - ```shell
      sudo yum install mysql
      ```
    - ```shell
      mysql -h <aurora db cluster url> -P 3306 -u <dbusername> -p
      ```
    - [Create database ncnt if it doesn’t exist](https://dev.mysql.com/doc/mysql-getting-started/en/)
5. [Update serverless.yml](https://serverless.com/framework/docs/providers/aws/guide/serverless.yml/)
    - Vpc:, securityGroupIds, subnetIds
    - Environment: database_url, database_driver, database_user, database_password
    - For local env use after you have [created a mysql database and added a ncnt db](https://dev.mysql.com/doc/mysql-getting-started/en/):
        - database_url: jdbc:mysql://localhost:3306/ncnt
        - database_driver: com.mysql.jdbc.Driver
        - database_user: root
        - database_password: <whatever password you set>
    - For production env use creds from above aws instances
6. [Setup env vars in serverless.yml](https://serverless.com/framework/docs/providers/aws/guide/variables/#referencing-environment-variables) and also [add to AWS SMPS](https://docs.aws.amazon.com/systems-manager/latest/userguide/systems-manager-paramstore.html) (ex: ${ssm:/ncnt/production/database/user})
7. [Install serverless](https://serverless.com/framework/docs/providers/aws/guide/installation/)
8. [Setup AWS Creds for Serverless Framework](https://serverless.com/framework/docs/providers/aws/guide/credentials/)
9. Create project in [https://dashboard.serverless.com](https://dashboard.serverless.com) if it doesn’t already exist
10. To deploy ./start.sh <env>
    ```shell 
    ./start.sh local
    ./start.sh production
    ```
## CI/CD
- Using [circleci](https://circleci.com/docs/2.0/first-steps/#section=getting-started) you can setup continuous integration and deployment. Look at /.circleci/config.yml
- Current configuration depends on the following:
    - all new pull requests will auto build and test
    - all pushes to master branch will deploy to development env
    - all pushes to production branch will deploy to production env
    - after any deployment, postman tests will get triggered
   
## Contributing
Feel free to create merge requests

## Author
Arya Soltanieh (@lostcodingsomewhere)

Contributions by:
Adam Foosner (@nucat176)
