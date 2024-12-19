# Jackfruit - ondemand Templete Extraction

Jackfruit is a Java application that listens to Kafka messages for error IDs. Upon receiving an error ID, it generates a credential request using an API, retrieves salt from the database, and then processes the credential request through another API.


# Installation

# Build the application:
Please build using the build script, this has been tested on jdk21, lower version support is not guaranteed.
`./build.sh`


# Configuration

All the configuration regarding Kafka, API endpoints, database details, and secrets are stored in `jackfruit/src/main/resources/ondemand-default.properties`.
Make sure to configure this file according to your environment.

The secrets are provided as environment variables.
Ensure that you set up the required environment variables before running the application.


You can optionally set an additional environment variable called `ONDEMAND_PROPERTIES_FILE_PATH` while running the container(or exec'ing the jar).
This will overwrite the default properties file. It accepts the path as uri schemes,
for e.g: to specify a local file you would `export ONDEMAND_PROPERTIES_FILE_PATH='file:///dmv/ondemand-default.properties'`
similarly files can be referred from http links too.


Example of running
```sh
## this file will overwrite the ondemand-default.properties file. It can overwrite
docker run  -e ONDEMAND_PROPERTIES_FILE_PATH='file:///dmv/ondemand-default.properties' -v "$(realpath dmv)":/dmv --rm -it iiitb/ondemand-template-extraction:tag  
```
