#!/bin/sh

set -e

#
# Automatically build and push the docs.
# Test.
#
echo "Pull request: '${TRAVIS_PULL_REQUEST}' on branch '${TRAVIS_BRANCH}'"
if [ "${TRAVIS_PULL_REQUEST}" = "false" ] && [ "${TRAVIS_BRANCH}" = "source" ]; then
    git config --global user.email "travis@travis-ci.org"
    git config --global user.name "travis-ci"
    git remote set-url origin https://${GH_OAUTH_TOKEN}:@github.com/robolectric/robolectric.github.io
    bundle exec middleman deploy --build-before
fi
