# --- Build Stage ---
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Optimizing Docker layer caching
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code to container
COPY src ./src

# Build the application jar
RUN mvn clean package -DskipTests

# --- Runtime Stage (Alpine-based) ---
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copy the jar from the builder stage
COPY --from=builder /app/target/finder-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]