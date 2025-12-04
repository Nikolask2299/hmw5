FROM openjdk:17-jdk-slim
COPY target/config-server.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]