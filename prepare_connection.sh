docker network create socialnet-net
docker network connect socialnet-net postgres
docker network connect socialnet-net socialnet
