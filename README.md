# MOSIP On-Demand Template Extraction Service

This service handles on-demand template extraction for the MOSIP platform.

## Prerequisites

- Java 21
- Maven 3.9+
- Docker
- Kubernetes cluster

## Building the Application

### Local Build
```bash
mvn clean install
```

### Docker Build
The application supports both AMD64 and ARM64 architectures. To build the Docker image:

```bash
# Build multi-arch image
docker buildx build --platform linux/amd64,linux/arm64 -t your-registry/mosip-odte:version --push .
```

## Deployment

### Kubernetes Deployment
The application can be deployed to Kubernetes using the manifests in the `k8s` directory:

```bash
kubectl apply -k k8s/
```

The deployment includes:
- Deployment configuration
- Service definition
- ConfigMap for application properties
- Kustomization for easy deployment

### Configuration
- Update the ConfigMap in `k8s/configmap.yaml` with your environment-specific values
- Secrets should be managed separately in your Kubernetes cluster

## Project Structure

```
.
├── Dockerfile           # Multi-arch Docker build configuration
├── pom.xml             # Maven project configuration
├── src/                # Source code
└── k8s/                # Kubernetes deployment manifests
    ├── configmap.yaml
    ├── deployment.yaml
    ├── kustomization.yaml
    └── service.yaml
```

## Latest Version
Current version: 1.2.6

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
