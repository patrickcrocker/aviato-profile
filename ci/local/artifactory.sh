#!/bin/bash
#
# Creates an Artifactory Docker container or starts an existing one.
#

set -e

imageName=artifactory

isRunning=`docker ps | grep $imageName >/dev/null 2>&1 && echo true || echo false`
if [ $isRunning = "false" ]; then

  isCreated=`docker ps -a | grep $imageName >/dev/null 2>&1 && echo true || echo false`
  if [ $isCreated = "false" ]; then
    docker create --name $imageName \
                  -p 8081:8081 \
                  jfrog-docker-reg2.bintray.io/jfrog/artifactory-oss:latest
  fi

  docker start $imageName
else
  echo "$imageName is already running"
fi
