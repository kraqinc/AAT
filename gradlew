#!/usr/bin/env bash
set -euo pipefail

GRADLE_VERSION="9.3.1"
GRADLE_DIST="gradle-${GRADLE_VERSION}-bin.zip"
GRADLE_URL="https://services.gradle.org/distributions/${GRADLE_DIST}"
CACHE_DIR="${GRADLE_USER_HOME:-$HOME/.gradle}/custom-gradle/${GRADLE_VERSION}"
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

if [[ ! -x "${CACHE_DIR}/bin/gradle" ]]; then
  mkdir -p "${CACHE_DIR%/*}"
  tmp_zip="$(mktemp)"
  echo "Downloading Gradle ${GRADLE_VERSION}..."
  curl -fsSL "${GRADLE_URL}" -o "${tmp_zip}"
  rm -rf "${CACHE_DIR}"
  mkdir -p "${CACHE_DIR}"
  unzip -q "${tmp_zip}" -d "${CACHE_DIR%/*}"
  mv "${CACHE_DIR%/*}/gradle-${GRADLE_VERSION}" "${CACHE_DIR}"
  rm -f "${tmp_zip}"
fi

exec "${CACHE_DIR}/bin/gradle" -p "${ROOT_DIR}" "$@"
