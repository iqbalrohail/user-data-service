# Use the official Java 11 base image
FROM adoptopenjdk/openjdk11:alpine-jre

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file into the container
COPY target/user-0.0.1-SNAPSHOT.jar app.jar

# Expose the port on which your application runs
EXPOSE 8080

# Set the command to run your application
CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]