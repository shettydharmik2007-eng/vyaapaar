# Build stage
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and package application
COPY src ./src
RUN mvn clean package -DskipTests -B

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy built fat JAR from builder stage
COPY --from=builder /app/target/vyaapaar-1.0.0-jar-with-dependencies.jar app.jar

# Expose default port
EXPOSE 8080

# Configure environment variables
ENV PORT=8080

# Launch Vyaapaar Web Application
CMD ["java", "-jar", "app.jar"]
