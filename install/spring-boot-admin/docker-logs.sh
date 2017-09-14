#! /bin/sh

APPLICATION_NAME=spring-boot-admin

echo "Logging $APPLICATION_NAME$1"

docker logs -f $APPLICATION_NAME$1
