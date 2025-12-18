FROM eclipse-temurin:25-jdk-alpine
WORKDIR /app

# Install maven
RUN apk add --no-cache maven

# Create a non-root user
RUN addgroup -g 1000 docker && \
    adduser -D -u 1000 -G docker docker

# Create .m2 directory with proper permissions
RUN mkdir -p /home/docker/.m2 && \
    chown -R docker:docker /home/docker/.m2

# Copy pom.xml first for dependency caching
COPY pom.xml ./
RUN chown -R docker:docker /app

# Switch to non-root user
USER docker

# Download dependencies with cache mount
RUN --mount=type=cache,target=/home/docker/.m2,uid=1000,gid=1000 \
    mvn dependency:go-offline -B

# Expose the application port and debug port
EXPOSE 8080 5005

# Run the application with auto-reload and remote debugging enabled
ENTRYPOINT ["mvn", "spring-boot:run", \
     "-Dspring-boot.run.jvmArguments=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"]