# using this as the openjdk is deprecated and no longer recommended for use https://hub.docker.com/_/openjdk
FROM maven:3.9-amazoncorretto-21 AS builder
ARG TAG_VERSION
LABEL VERSION="${TAG_VERSION}"
LABEL commit_hash=${COMMIT_ID}
LABEL build_time=${BUILD_TIME}
WORKDIR /ondemand/src

COPY src ./src
COPY pom.xml ./
RUN mvn clean install

FROM amazoncorretto:21
ARG COMMIT_ID
ARG BUILD_TIME
WORKDIR /ondemand

# required for groupadd
RUN yum install -y shadow-utils
RUN groupadd -g 1001 mosip && useradd -u 1001 -g 1001 -s /bin/sh -m mosip


# change permissions of file inside working dir
RUN chown -R mosip:mosip /ondemand

# select container user for all tasks
USER mosip

# Copy the jar from the builder stage
COPY --from=builder /ondemand/src/target/release-jar-with-dependencies.jar /ondemand/ondemand.jar

CMD ["java", "-jar", "/ondemand/ondemand.jar"]
