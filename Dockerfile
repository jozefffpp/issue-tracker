# Step 1: Use a base image with Java
FROM openjdk:17-jdk-slim

# Step 2: Set working directory inside container
WORKDIR /app

# Step 3: Copy the built jar into the container
COPY target/issue-tracker-0.0.1-SNAPSHOT.jar app.jar

# Step 4: Expose the port your app uses
EXPOSE 8080

# Step 5: Command to run your app
ENTRYPOINT ["java", "-jar", "app.jar"]