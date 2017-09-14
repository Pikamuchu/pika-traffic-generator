#!/bin/sh

set -e

echo "Environment Variables"
env

if [ "$1" = 'start' ]; then
  cd $APP_HOME
  echo "Starting traffic generator batch..."
  exec $JAVA_HOME/bin/java $JAVA_OPTS -jar -Dspring.profiles.active=$APP_PROFILE libs/app.jar
else
  exec "$@"
fi