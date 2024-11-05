#!/bin/bash

source conf/properties

echo "Stopping socialnet container."
docker stop socialnet &>> $BUILD_LOG_FILE

echo "Removing socialnet container."
docker rm socialnet &>> $BUILD_LOG_FILE

echo "Removing socialnet image."
docker rmi socialnet:$VERSION &>> $BUILD_LOG_FILE
