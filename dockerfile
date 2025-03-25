# Use the official OpenJDK 21 image as the base image
FROM eclipse-temurin:21-jdk

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven wrapper (if you're using it)
COPY .mvn .mvn
COPY mvnw pom.xml /app/

# Run Maven to download dependencies (useful for caching layers)
RUN ./mvnw dependency:go-offline

# Copy the entire project into the container
COPY . /app

# Build the project using Maven
RUN ./mvnw clean package -DskipTests

# Expose the port the app will run on (default is 8080)
EXPOSE 8080

# Set the default command to run the JAR (replace 'myapp-1.0.0.jar
CMD ["java", "-jar", "target/go-backend-0.0.1-SNAPSHOT.jar"]
