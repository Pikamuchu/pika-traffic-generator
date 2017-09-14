#! /bin/sh

APPLICATION_NAME=spring-boot-admin

echo "Stopping $APPLICATION_NAME$1"

docker stop $APPLICATION_NAME$1
