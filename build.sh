source conf/properties

mvn package

docker build -t socialnet:$VERSION .
