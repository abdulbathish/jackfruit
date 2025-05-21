# using this as the openjdk is deprecated and no longer recommended for use https://hub.docker.com/_/openjdk
# Build stage
FROM maven:3.9-amazoncorretto-21 AS builder
WORKDIR /ondemand/src

COPY src ./src
COPY pom.xml ./
RUN mvn clean install -DskipTests

# Runtime stage
FROM amazoncorretto:21-al2023
LABEL version="1.2.6"
LABEL build_date="2024-03-21"
WORKDIR /ondemand

# Create non-root user using Amazon Linux commands
RUN yum install -y shadow-utils && \
    groupadd -g 1001 mosip && \
    useradd -r -u 1001 -g mosip mosip && \
    yum remove -y shadow-utils && \
    yum clean all

# Set ownership and permissions
COPY --from=builder --chown=1001:1001 /ondemand/src/target/release-jar-with-dependencies.jar /ondemand/app.jar

# Use non-root user
USER 1001

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/ondemand/app.jar"]
