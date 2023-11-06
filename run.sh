source conf/properties

docker run -d --name socialnet -p $PORT:8080 socialnet:$VERSION
