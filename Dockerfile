# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:17-jre-slim
WORKDIR /app

# Install netcat for health checks
RUN apt-get update && apt-get install -y netcat-openbsd && rm -rf /var/lib/apt/lists/*

# Copy the built jar
COPY --from=build /app/target/*.jar app.jar

# Expose port
EXPOSE 8081

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD nc -z localhost 8081 || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]