# Stage 1: Build the application using Maven and OpenJDK 17
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the entire source code
COPY src ./src

# Package the application (skip tests for faster build)
RUN mvn clean package -DskipTests

# Stage 2: Run the application using OpenJDK 17
FROM openjdk:17

WORKDIR /app

# Copy the JAR from the build stage
COPY --from=build /app/target/DSC-System-Health-Monitor_V1.0.jar app.jar

# Expose application port (change if different)
EXPOSE 8086

# Run the application, optionally setting active Spring profile via environment variable
ENTRYPOINT ["java",  "-jar", "app.jar"]
