# Jackfruit - ondemand Templete Extraction

Jackfruit is a Java application that listens to Kafka messages for error IDs. Upon receiving an error ID, it generates a credential request using an API, retrieves salt from the database, and then processes the credential request through another API.


# Installation

# Build the application:
`mvn clean install`

# Configuration

All the configuration regarding Kafka, API endpoints, database details, and secrets are stored in `jackfruit/src/main/resources/ondemand-default.properties`.
Make sure to configure this file according to your environment.

The secrets are provided as environment variables.
Ensure that you set up the required environment variables before running the application.
