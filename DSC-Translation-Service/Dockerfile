# Stage 1: Build the application using Maven and OpenJDK 17
FROM maven:3.9.6-eclipse-temurin-17 as build

WORKDIR /app

# Copy the pom and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Package the application (skip tests to speed up build)
RUN mvn clean package -DskipTests

# Stage 2: Run the application using OpenJDK 17
FROM openjdk:17

WORKDIR /app

# Copy the compiled JAR from the build stage
COPY --from=build /app/target/DSC-Translation-Service_V1.0.jar app.jar

# Expose app port (change if needed)
EXPOSE 8085

# Enable Spring profiles through environment variable if needed
ENTRYPOINT ["java","-jar", "app.jar"]
