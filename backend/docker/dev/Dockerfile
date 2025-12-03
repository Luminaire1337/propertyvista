FROM eclipse-temurin:25-jdk-alpine
WORKDIR /app

# Install maven
RUN apk add --no-cache maven

# Set default permission
ARG DEFAULT_PERMISSION=755

# Create a non-root user to run the application
ARG USER_NAME=docker
ARG USER_ID=1000
ARG GROUP_NAME=docker
ARG GROUP_ID=1000
RUN addgroup -g ${GROUP_ID} ${GROUP_NAME} && \
    adduser -D -u ${USER_ID} -G ${GROUP_NAME} -s /bin/sh ${USER_NAME} && \
    chown -R ${USER_NAME}:${GROUP_NAME} /app && \
    chmod -R ${DEFAULT_PERMISSION} /app

# Copy the pom.xml
COPY --chown=${USER_NAME}:${GROUP_NAME} --chmod=${DEFAULT_PERMISSION} pom.xml .

# Change to the non-root user
USER ${USER_NAME}

# Expose the application port and debug port
EXPOSE 8080 5005

# Run the application with auto-reload and remote debugging enabled
# https://stackoverflow.com/a/69880735
ENTRYPOINT ["mvn", "spring-boot:run", \
     "-Dspring-boot.run.jvmArguments=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"]
