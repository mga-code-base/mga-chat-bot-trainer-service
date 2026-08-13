FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY target/mga-chat-bot-trainer-service-0.0.1.jar app.jar
EXPOSE 8086
ENTRYPOINT ["java", "-jar", "app.jar"]
