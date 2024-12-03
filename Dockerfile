# Step 1: Build stage
FROM maven:3.8.5-openjdk-8-slim as build

# Set the working directory
WORKDIR /app

# Copy the pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the source code and build the application
COPY src /app/src
RUN mvn clean package -DskipTests

# Step 2: Production stage
FROM openjdk:8-jre-slim

# Set the working directory
WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/sb-rest-api-0.0.1-SNAPSHOT.jar sb-rest-api.jar

# Expose the port the application will run on
EXPOSE 8090

# Command to run the Spring Boot application
ENTRYPOINT ["java", "-jar", "sb-rest-api.jar"]
