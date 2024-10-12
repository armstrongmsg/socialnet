#!/bin/bash

source conf/properties

echo "Building socialnet package."
mvn package -Dmaven.test.skip=true &>> $BUILD_LOG_FILE

if [ $? -ne 0 ]; then echo "Error on socialnet package build. Exiting." && exit; fi

echo "Building docker image."
docker build -t socialnet:$VERSION . &>> $BUILD_LOG_FILE

if [ $? -ne 0 ]; then echo "Error on socialnet docker image build. Exiting." && exit; fi

echo "Building socialnet media service package."
mkdir media-service-setup/
cd media-service-setup/
git clone https://github.com/armstrongmsg/socialnet-mediaservice.git &>> $BUILD_LOG_FILE
cd socialnet-mediaservice/
mvn package -DskipTests=true &>> $BUILD_LOG_FILE

echo "Building media service docker image."
docker build -t socialnet-mediaservice:$VERSION . &>> $BUILD_LOG_FILE

echo "Cleaning up set up directory"
cd ../../
rm -rf media-service-setup/
