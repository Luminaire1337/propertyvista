# Build stage
FROM maven:3-eclipse-temurin-25-alpine AS build
WORKDIR /app

# Copy the pom.xml and download the dependencies with cache
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 \
    mvn dependency:go-offline -B

# Copy the source code and build the application
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 \
    mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

# Install curl for healthcheck
RUN apk add --no-cache curl

# Create a non-root user to run the application
RUN addgroup -g 1000 docker && \
    adduser -D -u 1000 -G docker docker && \
    chown docker:docker /app

# Copy the jar file from the build stage
COPY --chown=docker:docker --from=build /app/target/*.jar app.jar

# Change to the non-root user
USER docker

# Expose the application port
EXPOSE 8080

# Add a health check
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]