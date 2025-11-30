#!/bin/bash
# Helper script to run Maven with Java 11
export JAVA_HOME=$(/usr/libexec/java_home -v 11)
mvn "$@"

