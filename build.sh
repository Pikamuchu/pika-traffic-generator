#!/bin/bash

APPLICATION_NAME=traffic-generator

# Clean build dir
echo "Cleaning build directory..."
rm -rf ./build/*

# Build application
echo ""
echo "Building application..."
./gradlew build --stacktrace

# Copy libs to install dir
echo ""
echo "Copying libs to install dir..."
cp -v build/libs/$APPLICATION_NAME*.jar install/$APPLICATION_NAME/libs/$APPLICATION_NAME.jar

# Build docker
if [ "$1" = "docker" ] || [ "$1" = "install" ]; then
	echo ""
	echo "Building docker..."
	docker build -t "pikamachu/$APPLICATION_NAME" .
fi

# Install application
if [ "$1" = "install" ]; then
	echo ""
	echo "Installing $APPLICATION_NAME..."
	cp -vR install/$APPLICATION_NAME ~
fi
