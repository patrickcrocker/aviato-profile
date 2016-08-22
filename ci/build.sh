#!/bin/bash

set -e

version=$(cat version/number)

pushd aviato-profile
  ./mvnw clean package -DversionNumber=$version
popd

cp aviato-profile/build/libs/aviato-profile-$VERSION.jar build-output/.
