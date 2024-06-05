# using this as the openjdk is deprecated and no longer recommended for use https://hub.docker.com/_/openjdk
FROM amazoncorretto:11
ARG TAG_VERSION
ARG COMMIT_ID
ARG BUILD_TIME
LABEL VERSION="${TAG_VERSION}"
LABEL commit_hash=${COMMIT_ID}
LABEL build_time=${BUILD_TIME}

# environment variable to pass active profile such as DEV, QA etc at docker runtime
ENV active_profile_env=${active_profile}

# required for groupadd
RUN yum install -y shadow-utils

# install packages and create user
RUN groupadd -g 1001 mosip && useradd -u 1001 -g 1001 -s /bin/sh -m mosip

WORKDIR /ondemand

# change permissions of file inside working dir
RUN chown -R mosip:mosip /ondemand

# select container user for all tasks
USER mosip

COPY target/kafka-trials-"${TAG_VERSION}"-jar-with-dependencies.jar /ondemand/ondemand.jar

CMD ["java", "-jar", "/ondemand/ondemand.jar"]
