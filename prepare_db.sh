#!/bin/bash

source conf/properties

psql -U $DATABASE_USERNAME -d $DATABASE_NAME -a -f src/main/sql/create_database.sql