#!/bin/bash

source conf/properties

docker stop postgres
docker rm postgres