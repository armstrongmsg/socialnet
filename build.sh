source conf/properties

echo "Building socialnet package."
mvn package &> $BUILD_LOG_FILE

echo "Building docker image."
docker build -t socialnet:$VERSION . &> $BUILD_LOG_FILE
