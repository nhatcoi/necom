#!/bin/bash

# Script to remove application configuration files from remote repository
# These files contain sensitive information and should not be committed

echo "Removing application configuration files from git tracking..."

# Remove files from git index (but keep local files)
git rm --cached necom-server/src/main/resources/application.yml
git rm --cached necom-server/src/main/resources/application-dev.yml
git rm --cached necom-server/src/main/resources/application-ci.yml

echo "Files removed from git tracking."
echo "Note: Local files are preserved. Commit this change to remove them from remote repository."
echo ""
echo "To commit this change, run:"
echo "  git commit -m 'Remove sensitive application configuration files'"
echo "  git push"

