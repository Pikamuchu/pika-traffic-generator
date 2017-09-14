#! /bin/sh

APPLICATION_NAME=traffic-generator

echo "Stopping $APPLICATION_NAME$1"

docker stop $APPLICATION_NAME$1
