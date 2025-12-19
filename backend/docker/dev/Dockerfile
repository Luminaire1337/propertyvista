FROM eclipse-temurin:21-jdk-noble
WORKDIR /app

# Create .m2 directory with proper permissions
RUN mkdir -p /home/ubuntu/.m2 && \
    chown -R ubuntu:ubuntu /home/ubuntu/.m2

# Copy Maven wrapper and pom.xml
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN mkdir -p /app/src && \
  chown -R ubuntu:ubuntu /app && \
  chmod +x mvnw

# Switch to non-root user
USER ubuntu

# Expose the application port and debug port
EXPOSE 8080 5005

# Run the application with auto-reload and remote debugging enabled
ENTRYPOINT ["./mvnw", "spring-boot:run", \
     "-Dspring-boot.run.jvmArguments=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"]