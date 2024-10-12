#!/bin/bash

source conf/properties

bash deploy/prepare_db.sh

echo "Starting socialnet container."
docker run -d --name socialnet -p $PORT:8080 socialnet:$VERSION &>> $BUILD_LOG_FILE

bash deploy/prepare_connection.sh
