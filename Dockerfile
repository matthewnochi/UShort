# Stage 1: Build with Maven and OpenJDK 17
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY .mvn/ ./mvn
COPY mvnw pom.xml ./
RUN mvn dependency:go-offline

# Copy source code and build
COPY src ./src
RUN mvn clean package 

# Stage 2: Run the application using a slim JDK image
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

# Copy the jar file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Define the entrypoint for the container
ENTRYPOINT ["java", "-XX:+EnableDynamicAgentLoading", "-jar", "app.jar"]
