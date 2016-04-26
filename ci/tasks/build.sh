---
platform: linux

image_resource:
  type: docker-image
  source:
    repository: java
    tag: '8'

params:
  MAVEN_OPTS:
  MAVEN_CONFIG:

inputs:
  - name: aviato-profile

run:
  path: aviato-profile/ci/tasks/unit.sh
  args: [
    --input-dir, aviato-profile
  ]
