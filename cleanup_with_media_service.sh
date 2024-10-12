#!/bin/bash

source conf/properties

echo "Stopping socialnet container."
docker stop socialnet &>> $BUILD_LOG_FILE

echo "Stopping socialnet media service container."
docker stop socialnet-mediaservice &>> $BUILD_LOG_FILE

echo "Removing socialnet container."
docker rm socialnet &>> $BUILD_LOG_FILE

echo "Removing socialnet media service container."
docker rm socialnet-mediaservice &>> $BUILD_LOG_FILE

echo "Removing socialnet image."
docker rmi socialnet:$VERSION &>> $BUILD_LOG_FILE

echo "Removing socialnet media service image."
docker rmi socialnet-mediaservice:$VERSION &>> $BUILD_LOG_FILE

source deploy/reset_db.sh

echo "Removing docker network."
docker network rm socialnet-net &>> $BUILD_LOG_FILE
