#!/bin/bash
# Helper script to run Maven with Java 17 (Spring Boot 3)
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
mvn "$@"
