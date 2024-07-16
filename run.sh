source conf/properties

bash prepare_db.sh

echo "Starting socialnet container."
docker run -d --name socialnet -p $PORT:8080 socialnet:$VERSION &>> $BUILD_LOG_FILE

bash prepare_connection.sh
