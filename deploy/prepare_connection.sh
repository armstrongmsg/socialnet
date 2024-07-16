#!/bin/bash

source conf/properties

echo "Creating docker network."
docker network create socialnet-net &>> $BUILD_LOG_FILE

echo "Connecting socialnet container to network."
docker network connect socialnet-net postgres &>> $BUILD_LOG_FILE

echo "Connecting database container to network."
docker network connect socialnet-net socialnet &>> $BUILD_LOG_FILE
