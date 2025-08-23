FROM openjdk:17-jdk

WORKDIR /app

# Copy the Spring Boot fat jar
COPY ./target/*.jar /app.jar

# Copy Firebase service account key (created in GitHub Actions runner)
COPY firebase-service-account.json /app/config/firebase-service-account.json

# Set environment variable for Google SDK
ENV GOOGLE_APPLICATION_CREDENTIALS=/app/config/firebase-service-account.json

# (Optional) Lock file permissions
RUN chmod 400 /app/config/firebase-service-account.json

EXPOSE 8080

CMD ["java", "-jar", "/app.jar"]
