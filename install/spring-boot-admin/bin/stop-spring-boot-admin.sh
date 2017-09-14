#!/bin/sh
. ~/.bash_profile

pid=`ps -ef | grep java | grep "spring-boot-admin" | grep "$(whoami)" | awk '{ print $2 }'`

if [ "X$pid" = "X" ]
then
   echo "Spring boot admin not running; exiting ...."
   exit 1
fi

echo "Stopping spring boot admin ($pid)"
kill -9 $pid
