source conf/properties

bash prepare_db.sh

docker run -d --name socialnet -p $PORT:8080 socialnet:$VERSION

bash prepare_connection.sh
