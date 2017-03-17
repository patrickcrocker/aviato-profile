#!/bin/ash

set -e

version=$(cat version/number)

args="-DversionNumber=$version"
[ -n "$MAVEN_REPO_MIRROR" ] && args="$args -Drepository.url=$MAVEN_REPO_MIRROR";
[ -n "$MAVEN_REPO_USERNAME" ] && args="$args -Drepository.username=$MAVEN_REPO_USERNAME";
[ -n "$MAVEN_REPO_PASSWORD" ] && args="$args -Drepository.password=$MAVEN_REPO_PASSWORD";

cd aviato-profile
# todo: run some integration tests
#./mvnw verify
cd ..
