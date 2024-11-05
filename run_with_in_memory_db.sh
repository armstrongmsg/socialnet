#!/bin/bash

source conf/properties

echo "Starting socialnet container."
docker run -d --name socialnet -p $PORT:8080 socialnet:$VERSION &>> $BUILD_LOG_FILE
