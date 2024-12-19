# Jackfruit - On-demand Template Extraction

Jackfruit is a Java application that listens for Kafka messages containing error IDs. Upon receiving an error ID, it generates a credential request using an API, retrieves salt from the database, and processes the credential request through another API.

---

## Installation

### Build the application:
To build the application, use the provided build script. It has been tested with JDK 21, and support for lower versions is not guaranteed.

```sh
./build.sh
```

### Configuration

All configuration details for Kafka, API endpoints, database information, and secrets are stored in jackfruit/src/main/resources/ondemand-default.properties. Make sure to adjust this file to match your environment.

The application requires secrets to be provided as environment variables. **Ensure these are set up before running the application**

You can also specify a custom properties file by setting the `ONDEMAND_PROPERTIES_FILE_PATH` environment variable. This will overwrite the default ondemand-default.properties file. The path can be provided using various URI schemes. For example, to specify a local file:
```sh
export ONDEMAND_PROPERTIES_FILE_PATH='file:///dmv/ondemand-default.properties'
```
You can also use HTTP links to specify the properties file.

### Example Docker run Command
```sh
docker run -e ONDEMAND_PROPERTIES_FILE_PATH='file:///dmv/ondemand-default.properties' -v "$(realpath dmv)":/dmv --rm -it iiitb/ondemand-template-extraction:tag
```
