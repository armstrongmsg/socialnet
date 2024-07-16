#!/bin/bash

source conf/properties

echo "Stopping database container."
docker stop postgres &> $BUILD_LOG_FILE

echo "Removing database container."
docker rm postgres &> $BUILD_LOG_FILE