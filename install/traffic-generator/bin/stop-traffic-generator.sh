#!/bin/sh
. ~/.bash_profile

pid=`ps -ef | grep java | grep "traffic-generator" | grep "$(whoami)" | awk '{ print $2 }'`

if [ "X$pid" = "X" ]
then
   echo "Traffic generator batch not running; exiting ...."
   exit 1
fi

echo "Stoping traffic generator batch ($pid)"
kill -9 $pid
