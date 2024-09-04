# socialnet

## Description

A prototype of social network made with JSF.

## Requirements

- Docker

## How to use

- Fill in src/main/resources/application.properties configuration file with the application configuration.
- Fill in src/main/resources/META-INF/persistence.xml configuration file with the database access parameters.
- Fill in conf/properties configuration file with deploy options.
- Run the build.sh script. It will build the application docker image.
- Run the run.sh script. It will create the database and the application containers.
- Access http://localhost:8888/socialnet/home.jsf in a browser.