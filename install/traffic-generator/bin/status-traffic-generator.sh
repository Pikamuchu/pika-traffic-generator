#!/bin/sh
echo "Status traffic generator batch:"
ps -ef | grep java | grep "traffic-generator" | grep "$(whoami)"
