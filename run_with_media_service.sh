#!/bin/bash

source conf/properties

bash deploy/prepare_db.sh

echo "Starting socialnet media service"
docker run -d --name socialnet-mediaservice -p $MEDIA_SERVICE_PORT:8080 socialnet-mediaservice:$VERSION &>> $BUILD_LOG_FILE

echo "Starting socialnet container."
docker run -d --name socialnet -p $PORT:8080 socialnet:$VERSION &>> $BUILD_LOG_FILE

bash deploy/prepare_connection_with_media_service.sh