#!/bin/bash
#
# All CF_* variables are provided externally from this script

set -e

# copy the artifact to the task-output folder
cp release/$ARTIFACT_GLOB generate-manifest-output/.

pushd generate-manifest-output

# convert 'artifact-*.jar' into 'artifact-1.0.0-rc.1.jar'
appPath=$(ls $ARTIFACT_GLOB)

cat <<EOF >manifest.yml
---
applications:
- name: $CF_APP_NAME
  host: $CF_APP_HOST
  path: $appPath
  memory: ${CF_APP_MEMORY:-512M}
  instances: ${CF_APP_INSTANCES:-1}
  timeout: ${CF_APP_TIMEOUT:-180}
  services: [ $CF_APP_SERVICES ]
  env:
    JAVA_OPTS: -Djava.security.egd=file:///dev/urandom
EOF

if [ "true" = "$CF_SKIP_SSL" ]; then
  echo "    CF_TARGET: $CF_API_URL" >> manifest.yml
fi

echo "Generated manifest:"
cat manifest.yml

popd
