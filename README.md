# Kotlin Serverless (API) Framework

 * [Overview](#overview)
 * [Installation](#install)
 * [Creating new service functions](#forking)
 * [Deploy (Local and Cloud)](#deploy)
 * [Contribute](#contributing)

## Overview

Kotlin Serverless (API) Framework helps to rapidly build and deploy a serverless API
Make minor changes in order to simply add your new endpoint
Easily deploy locally or to production via a simple script
Deploy process also runs all tests

### Tech stack:
- Serverless, Kotlin, Spek.
Todo:
- postgres (auroraserverless), circleci

## Install

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
   ./start.sh
   ```
   
## Forking

Since this is a template/framework, you are expected to fork.
In order to add new api endpoint (To non existing object class)

- Add a folder under /src/main/{object_name} and add under it (models, services)
- Add your appropriate model under models, similar to User.kt
- Update the RequestDispatcher.locate (when statement) if you are adding a new service/model
- Add {object_name}Service.kt (This will have your new functionality)
- If you are adding a new non standard CRUD endpoint, simply add it as a method {method_name}
- Edit serverless.yml
```yaml
functions:
  {object_name}:
    handler: kotlinserverless.framework.Handler
    events:
      - http:
          path: /{object_name}/{method_name}
          method: get #get/post/etc
```
- Edit /src/main/resources/routes.yml
```yaml
# =========================
  # {object_name} Microservice
  # =========================
  
  - regex: '^/{object_name}(/*[a-zA-Z0-9]*)?'
    cls: kotlinserverless.main.{object_name}s.controllers.{Object_name}Controller
    func: execute
```
- Add appropriate Unit test to /main/test/unit
- test via 
```shell
mvn clean test
```

### if you wish to make changes to an existing object, simply:
- add new function to the appropriate service
- add new unit test
- update the serverless.yml

## Deploy

- Locally
```shell
   ./start.sh local
   ```

- Production
```shell
   ./start.sh production
   ```

## Contributing
Feel free to create merge requests
