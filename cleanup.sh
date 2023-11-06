source conf/properties

docker stop socialnet
docker rm socialnet
docker rmi socialnet:$VERSION
