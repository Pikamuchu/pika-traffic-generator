#!/bin/bash
. ~/.bash_profile

APPLICATION_NAME=traffic-generator
APPLICATION_PORT=8090
APPLICATION_HOME=$(dirname "$0")
APPLICATION_PROFILE=test
APPLICATION_JAVA_OPTS=

if [ "$TRAFFIC_GENERATOR_HOMEx" != "x" ]; then
    APPLICATION_HOME=$TRAFFIC_GENERATOR_HOME
fi

if [ "$TRAFFIC_GENERATOR_PROFILEx" != "x" ]; then
    APPLICATION_PROFILE=$TRAFFIC_GENERATOR_PROFILE
fi

if [ "$JAVA_OPTSx" != "x" ]; then
    APPLICATION_JAVA_OPTS=$JAVA_OPTS
fi

if [ "$1x" != "x" ]; then
    APPLICATION_PORT=$1
fi

if [ "$2x" != "x" ]; then
    APPLICATION_PROFILE=$2
fi

echo "Starting $APPLICATION_NAME$1..."
echo "APPLICATION_PORT=$APPLICATION_PORT"
echo "APPLICATION_HOME=$APPLICATION_HOME"
echo "APPLICATION_PROFILE=$APPLICATION_PROFILE"

# Go to traffic generator home directory
cd $TRAFFIC_GENERATOR_HOME

# Starting traffic generator batch
nohup $JAVA_HOME/bin/java $APPLICATION_JAVA_OPTS -jar -Dspring.profiles.active=$APPLICATION_PROFILE -Dserver.port=$APPLICATION_PORT libs/$APPLICATION_NAME.jar > /dev/null 2>&1 &
