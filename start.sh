#!/bin/bash

mvn package

serverless deploy --stage $1

# Not required, but good way to test if it is up and running properly
serverless invoke $1 --function user --data '{"path": "/user/hello"}'