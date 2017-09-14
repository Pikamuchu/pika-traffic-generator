#! /bin/sh

APPLICATION_NAME=traffic-generator

echo "Logging $APPLICATION_NAME$1"

docker logs -f $APPLICATION_NAME$1
