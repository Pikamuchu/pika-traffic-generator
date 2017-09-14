#!/bin/bash
. ~/.bash_profile

# Go to traffic generator home directory
cd $SPRING_BOOT_ADMIN_HOME

# Starting Spring boot admin
echo "Starting Spring boot admin..."
nohup $JAVA_HOME/bin/java $JAVA_OPTS -jar -Dspring.profiles.active=$SPRING_BOOT_ADMIN_PROFILE libs/spring-boot-admin.jar > /dev/null 2>&1 &
