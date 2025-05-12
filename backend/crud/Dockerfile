FROM openjdk:19-jdk-alpine

# Install necessary tools
RUN apk add --no-cache curl vim

# Create a non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Set the working directory
WORKDIR /app

# Copy the application.properties file explicitly
COPY src/main/resources/application.properties /app/config/application.properties

# Copy the application JAR file
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# Set Java options to explicitly point to application.properties
ENV SPRING_CONFIG_LOCATION=file:/app/config/application.properties

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
