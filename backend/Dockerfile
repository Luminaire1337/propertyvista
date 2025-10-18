# Let's start, by building our Spring Boot app and turning it into a jar file.
FROM maven:3-eclipse-temurin-25-alpine AS build
WORKDIR /app

# Copy the pom.xml and download the dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code and build the application
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

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

# Copy the jar file from the build stage
COPY --chown=${USER_NAME}:${GROUP_NAME} --chmod=${DEFAULT_PERMISSION} --from=build /app/target/*.jar app.jar

# Change to the non-root user
USER ${USER_NAME}

# Expose the application port
EXPOSE 8080

# Add a health check
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
