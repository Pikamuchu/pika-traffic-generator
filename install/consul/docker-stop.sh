#! /bin/sh

APPLICATION_NAME=consul

echo "Stopping $APPLICATION_NAME$1"

docker stop $APPLICATION_NAME$1
