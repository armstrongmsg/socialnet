#!/bin/bash

source conf/properties

echo "Building socialnet package."
mvn package -Dmaven.test.skip=true &>> $BUILD_LOG_FILE

if [ $? -ne 0 ]; then echo "Error on socialnet package build. Exiting." && exit; fi

echo "Building docker image."
docker build -t socialnet:$VERSION . &>> $BUILD_LOG_FILE

if [ $? -ne 0 ]; then echo "Error on socialnet docker image build. Exiting." && exit; fi
