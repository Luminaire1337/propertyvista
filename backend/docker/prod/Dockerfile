# Build stage
FROM eclipse-temurin:21-jdk-noble AS build
WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw

RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw dependency:go-offline -B

# Copy the source code and build the application
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-noble
WORKDIR /app

# Set up permissions for a non-root user
RUN chown -R ubuntu:ubuntu /app

# Copy the jar file from the build stage
COPY --chown=ubuntu:ubuntu --from=build /app/target/*.jar app.jar

# Change to the non-root user
USER ubuntu

# Expose the application port
EXPOSE 8080

# Add a health check
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]