#!/bin/bash

source conf/properties

docker run --name postgres -e POSTGRES_PASSWORD="$DATABASE_PASSWORD" -p "$DATABASE_PORT":5432 -d postgres

docker cp src/main/sql/create_database.sql postgres:/tmp

until docker exec -u postgres postgres psql -l >> /dev/null
do
    sleep 1
done

docker exec -u postgres postgres psql -U postgres -d postgres -a -f /tmp/create_database.sql
