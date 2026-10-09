# Stage 1: Build the Spring Boot application using Maven
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy project definition and source code
COPY pom.xml .
COPY src ./src

# Build package while skipping tests for faster deployment
RUN mvn clean package -DskipTests

# Stage 2: Create lightweight runtime image with JRE 21
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy compiled jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose server port
EXPOSE 8080

# Run Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]