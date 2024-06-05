#!/usr/bin/env bash
set -euo pipefail
DOCKER_CMD="/usr/bin/docker"
TAG_VERSION=$(git describe --tags --abbrev=0)
DOCKER_IMAGE="abdulbathish/mosip-ondemand:${TAG_VERSION}"
BUILD_TIME=$(date --iso-8601=seconds)
COMMIT_ID=$(git rev-list --abbrev-commit -n 1 ${TAG_VERSION})

#mvn release:prepare
if ! docker image inspect "${DOCKER_IMAGE}" >/dev/null 2>&1
then
  "${DOCKER_CMD}" build --build-arg COMMIT_ID=${COMMIT_ID} --build-arg BUILD_TIME="${BUILD_TIME}" --build-arg TAG_VERSION="${TAG_VERSION}" -t "${DOCKER_IMAGE}" .
fi

"${DOCKER_CMD}" image push "$DOCKER_IMAGE"
