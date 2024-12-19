#!/usr/bin/env bash
set -euo pipefail

DOCKER_CMD=${DOCKER_CMD:-"/usr/bin/docker"}
COMMIT_ID=$(git rev-list --abbrev-commit -n 1 HEAD)
TAG_VERSION="$COMMIT_ID"
DOCKER_IMAGE=${DOCKER_IMAGE:-"iiitb/ondemand-template-extraction:${TAG_VERSION}"}
BUILD_TIME=$(date --iso-8601=seconds)

if ! docker image inspect "${DOCKER_IMAGE}" >/dev/null 2>&1
then
  "${DOCKER_CMD}" build --build-arg COMMIT_ID=${COMMIT_ID} --build-arg BUILD_TIME="${BUILD_TIME}" --build-arg TAG_VERSION="${TAG_VERSION}" -t "${DOCKER_IMAGE}" .
fi
