#!/bin/bash

source conf/properties

echo "Creating database container."
docker run --name postgres -e POSTGRES_PASSWORD="$DATABASE_PASSWORD" -p "$DATABASE_PORT":5432 -d postgres &>> $BUILD_LOG_FILE

docker cp src/main/sql/create_database.sql postgres:/tmp &>> $BUILD_LOG_FILE

echo "Waiting for database to start up."
until docker exec -u postgres postgres psql -l &>> $BUILD_LOG_FILE
do
    sleep 1
done

echo "Setting up database."
docker exec -u postgres postgres psql -U postgres -d postgres -a -f /tmp/create_database.sql &>> $BUILD_LOG_FILE
