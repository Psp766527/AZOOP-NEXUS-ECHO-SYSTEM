# Stage 1: Build the application using Maven and OpenJDK 17
FROM maven:3.9.6-eclipse-temurin-17 as build

WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the entire source code
COPY src ./src

# Build the application and skip tests
RUN mvn clean package -DskipTests

# Stage 2: Run the application using OpenJDK 17
FROM openjdk:17

WORKDIR /app

# Copy the packaged JAR from the build stage
COPY --from=build /app/target/DSC-API-Gateway_V1.0.jar app.jar

# Expose the default gateway port (adjust if needed)
EXPOSE 8088

# Enable active Spring profile from environment variable (optional)
ENTRYPOINT ["java", "-jar", "app.jar"]
