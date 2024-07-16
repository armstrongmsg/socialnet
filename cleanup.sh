source conf/properties

docker stop socialnet
docker rm socialnet
docker rmi socialnet:$VERSION

bash reset_db.sh

docker network rm socialnet-net
